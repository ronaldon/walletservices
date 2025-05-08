package com.recargapay.walletservices.application.services;

import com.recargapay.walletservices.adapters.persistence.WalletRepository;
import com.recargapay.walletservices.adapters.persistence.entity.WalletEntity;
import com.recargapay.walletservices.application.port.WalletServicesPort;
import com.recargapay.walletservices.domain.exceptions.BusinessException;
import com.recargapay.walletservices.domain.model.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService implements WalletServicesPort {
    private final WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(String userId, String name) {
        // Validate if wallet already exists for this user
        if (walletRepository.existsByUserId(userId)) {
            throw new BusinessException("User already has a wallet", "W2");
        }

        // Validate inputs
        validateWalletCreation(userId, name);

        WalletEntity walletEntity = WalletEntity.builder()
                .userId(userId)
                .name(name)
                .build();

        walletEntity = walletRepository.save(walletEntity);

        return Wallet.builder()
            .id(walletEntity.getId().toString())
            .name(walletEntity.getName())
            .userId(walletEntity.getUserId()).build();
    }

    private void validateWalletCreation(String userId, String name) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("User ID cannot be empty", "W3");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Wallet name cannot be empty", "W4");
        }
    }
}