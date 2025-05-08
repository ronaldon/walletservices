package com.walletservices.application.services;

import com.walletservices.adapters.persistence.TransactionRepository;
import com.walletservices.adapters.persistence.WalletRepository;
import com.walletservices.adapters.persistence.entity.WalletTransactionEntity;
import com.walletservices.application.port.WalletTransactionServicePort;
import com.walletservices.domain.exceptions.BusinessException;
import com.walletservices.domain.model.WalletTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService implements WalletTransactionServicePort {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final LockRegistry lockRegistry;

    private static final String LOCK_KEY_PREFIX = "wallet:transaction:";
    private static final long LOCK_TIMEOUT = 10; // seconds

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException("Amount cannot be null", "T2");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Amount must be greater than zero", "T3");
        }
    }

    private BigDecimal calculateNewBalance(BigDecimal currentBalance, BigDecimal amount, WalletTransactionEntity.TransactionType type) {
        BigDecimal transactionAmount = type == WalletTransactionEntity.TransactionType.WITHDRAW ? amount.negate() : amount;
        BigDecimal newBalance = currentBalance.add(transactionAmount);
        
        if (type == WalletTransactionEntity.TransactionType.WITHDRAW && newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Insufficient funds", "T1");
        }
        
        return newBalance;
    }

    private WalletTransaction createTransaction(Long walletId, BigDecimal amount, BigDecimal balance, WalletTransactionEntity.TransactionType type) {
        var transactionEntity = transactionRepository.save(WalletTransactionEntity.builder()
                .amount(type == WalletTransactionEntity.TransactionType.WITHDRAW ? amount.negate() : amount)
                .balance(balance)
                .type(type)
                .timestamp(LocalDateTime.now())
                .walletId(walletId)
                .build());

        return WalletTransaction.builder()
            .amount(transactionEntity.getAmount())
            .balance(transactionEntity.getBalance())
            .type(transactionEntity.getType().name())
            .id(transactionEntity.getId())
            .build();
    }

    private Lock obtainLock(Long accountID) {
        Lock lock = lockRegistry.obtain(LOCK_KEY_PREFIX + accountID);
        try {
            if (!lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
                throw new BusinessException("Lock acquisition timeout. Please try again.", "T4");
            }
            return lock;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Lock acquisition interrupted", "T5");
        }
    }

    private void releaseLock(Lock lock) {
        try {
            if (lock != null) {
                lock.unlock();
            }
        } catch (Exception e) {
            log.error("Error releasing lock for key" , e);
        }
    }

    private WalletTransaction doTransaction(Long walletId, BigDecimal amount, WalletTransactionEntity.TransactionType type) {
        validateAmount(amount);
        
        walletRepository.findById(walletId)
                .orElseThrow(() -> new BusinessException("Wallet not found", "W1"));

        Lock lock = null;
        
        try {
            lock = obtainLock(walletId);
            
            BigDecimal currentBalance = transactionRepository.findCurrentBalance(walletId)
                    .map(WalletTransactionEntity::getBalance)
                    .orElse(BigDecimal.ZERO);

            BigDecimal newBalance = calculateNewBalance(currentBalance, amount, type);
            return createTransaction(walletId, amount, newBalance, type);
            
        } finally {
            releaseLock(lock);
        }
    }

    @Transactional
    public WalletTransaction deposit(Long walletId, BigDecimal amount) {
        return this.doTransaction(walletId, amount, WalletTransactionEntity.TransactionType.DEPOSIT);
    }

    @Transactional
    public WalletTransaction withdraw(Long walletId, BigDecimal amount) {
        return this.doTransaction(walletId, amount, WalletTransactionEntity.TransactionType.WITHDRAW);
    }

    @Transactional
    public void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount) {
        // Use a consistent order of locking to prevent deadlocks
        Long firstLockId = Math.min(fromWalletId, toWalletId);
        Long secondLockId = Math.max(fromWalletId, toWalletId);
        
        Lock firstLock = null;
        Lock secondLock = null;
        
        try {
            firstLock = obtainLock(firstLockId);
            secondLock = obtainLock(secondLockId);
            
            this.withdraw(fromWalletId, amount);
            this.deposit(toWalletId, amount);
            
        } finally {
            releaseLock(secondLock);
            releaseLock(firstLock);
        }
    }

    public BigDecimal getBalance(Long walletId) {
        Optional<WalletTransactionEntity> entity = transactionRepository.findCurrentBalance(walletId);
        return entity.map(WalletTransactionEntity::getBalance).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getHistoricalBalance(Long walletId, LocalDateTime timestamp) {
        Optional<WalletTransactionEntity> entity = transactionRepository.findLatestBalanceByTimestamp(walletId, timestamp);
        return entity.map(WalletTransactionEntity::getBalance).orElse(BigDecimal.ZERO);
    }
}
