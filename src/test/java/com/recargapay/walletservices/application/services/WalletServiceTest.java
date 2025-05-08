package com.recargapay.walletservices.application.services;

import com.recargapay.walletservices.adapters.persistence.WalletRepository;
import com.recargapay.walletservices.adapters.persistence.entity.WalletEntity;
import com.recargapay.walletservices.domain.model.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private WalletEntity walletEntity;

    @BeforeEach
    void setUp() {
        walletEntity = WalletEntity.builder()
                .id(1L)
                .userId("user123")
                .name("Test Wallet")
                .build();
    }

    @Test
    void createWallet_ShouldCreateAndReturnWallet() {
        // Arrange
        when(walletRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        // Act
        Wallet result = walletService.createWallet("user123", "Test Wallet");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("1");
        assertThat(result.userId()).isEqualTo("user123");
        assertThat(result.name()).isEqualTo("Test Wallet");
    }
} 