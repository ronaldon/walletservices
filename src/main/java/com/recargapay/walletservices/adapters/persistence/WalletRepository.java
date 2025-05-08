package com.recargapay.walletservices.adapters.persistence;

import com.recargapay.walletservices.adapters.persistence.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
    boolean existsByUserId(String userId);
}
