package com.walletservices.adapters.persistence;

import com.walletservices.adapters.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    boolean existsByUserId(String userId);
}
