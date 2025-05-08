package com.recargapay.walletservices.application.services;

import com.recargapay.walletservices.adapters.persistence.TransactionRepository;
import com.recargapay.walletservices.adapters.persistence.WalletRepository;
import com.recargapay.walletservices.adapters.persistence.entity.WalletEntity;
import com.recargapay.walletservices.adapters.persistence.entity.WalletTransactionEntity;
import com.recargapay.walletservices.domain.exceptions.BusinessException;
import com.recargapay.walletservices.domain.model.WalletTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.locks.LockRegistry;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private LockRegistry lockRegistry;

    @Mock
    private Lock lock;

    @InjectMocks
    private TransactionService transactionService;

    private WalletTransactionEntity transactionEntity;
    private final Long WALLET_ID = 1L;
    
    private final WalletEntity WALLET_ENTITY = WalletEntity.builder().id(WALLET_ID).build();

    @BeforeEach
    void setUp() throws Exception {
        transactionEntity = WalletTransactionEntity.builder()
                .id(1L)
                .walletId(WALLET_ID)
                .amount(BigDecimal.valueOf(100))
                .balance(BigDecimal.valueOf(100))
                .type(WalletTransactionEntity.TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void deposit_ShouldCreateDepositTransaction() throws InterruptedException {
        // Arrange
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(WALLET_ENTITY));
        when(transactionRepository.findCurrentBalance(WALLET_ID)).thenReturn(Optional.empty());
        when(transactionRepository.save(any())).thenReturn(transactionEntity);

        // Mock lock behavior
        when(lockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(lock).unlock();
        // Act
        WalletTransaction result = transactionService.deposit(WALLET_ID, BigDecimal.valueOf(100));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.amount()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(result.type()).isEqualTo(WalletTransactionEntity.TransactionType.DEPOSIT.name());
    }

    @Test
    void withdraw_WithSufficientFunds_ShouldCreateWithdrawTransaction() throws InterruptedException {
        // Arrange
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(WALLET_ENTITY));
        when(transactionRepository.findCurrentBalance(WALLET_ID))
                .thenReturn(Optional.of(transactionEntity));
        when(transactionRepository.save(any())).thenReturn(transactionEntity);
        when(transactionRepository.save(any())).thenReturn(transactionEntity);

        // Mock lock behavior
        when(lockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(lock).unlock();
        // Act
        WalletTransaction result = transactionService.withdraw(WALLET_ID, BigDecimal.valueOf(50));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo(WalletTransactionEntity.TransactionType.DEPOSIT.name());
        
        // Verify lock interactions
        verify(lockRegistry).obtain(anyString());
        verify(lock).tryLock(anyLong(), any(TimeUnit.class));
        verify(lock).unlock();
    }

    @Test
    void withdraw_WithInsufficientFunds_ShouldThrowException() throws InterruptedException {
        // Arrange
        when(walletRepository.findById(WALLET_ID)).thenReturn(Optional.of(WALLET_ENTITY));
        when(transactionRepository.findCurrentBalance(WALLET_ID))
                .thenReturn(Optional.of(transactionEntity));
        // Mock lock behavior
        when(lockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(lock).unlock();
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> 
            transactionService.withdraw(WALLET_ID, BigDecimal.valueOf(150))
        );
        assertThat(exception.getErrorCode()).isEqualTo("T1");
    }

    @Test
    void getBalance_ShouldReturnCurrentBalance() {
        // Arrange
        when(transactionRepository.findCurrentBalance(WALLET_ID))
                .thenReturn(Optional.of(transactionEntity));

        // Act
        BigDecimal result = transactionService.getBalance(WALLET_ID);

        // Assert
        assertThat(result).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void getBalance_WhenNoTransactions_ShouldReturnZero() {
        // Arrange
        when(transactionRepository.findCurrentBalance(WALLET_ID))
                .thenReturn(Optional.empty());

        // Act
        BigDecimal result = transactionService.getBalance(WALLET_ID);

        // Assert
        assertThat(result).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void transfer_ShouldCreateTwoTransactions() throws InterruptedException {
        // Arrange
        Long toWalletId = 2L;
        when(walletRepository.findById(any())).thenReturn(Optional.of(WalletEntity.builder().id(toWalletId).build()));
        when(transactionRepository.findCurrentBalance(any()))
                .thenReturn(Optional.of(transactionEntity));
        when(transactionRepository.save(any())).thenReturn(transactionEntity);
    
        // Mock lock behavior
        when(lockRegistry.obtain(anyString())).thenReturn(lock);
        when(lock.tryLock(anyLong(), any(TimeUnit.class))).thenReturn(true);
        doNothing().when(lock).unlock();
        // Act
        transactionService.transfer(WALLET_ID, toWalletId, BigDecimal.valueOf(50));

        verify(transactionRepository).findCurrentBalance(WALLET_ID);
        verify(transactionRepository, times(2)).save(any());
    }

    @Test
    void deposit_WithNegativeAmount_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
            transactionService.deposit(WALLET_ID, BigDecimal.valueOf(-100))
        );
        assertThat(exception.getErrorCode()).isEqualTo("T3");
    }

    @Test
    void deposit_WithNullAmount_ShouldThrowException() {
        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () ->
            transactionService.deposit(WALLET_ID, null)
        );
        assertThat(exception.getErrorCode()).isEqualTo("T2");
    }
} 