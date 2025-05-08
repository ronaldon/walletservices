package com.walletservices.adapters.persistence;

import com.walletservices.adapters.persistence.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<WalletTransactionEntity, Long> {
    @Query("""
        SELECT t
        FROM WalletTransactionEntity t
        WHERE t.walletId = :walletId
        AND t.timestamp <= :timestamp ORDER BY t.timestamp DESC LIMIT 1
    """)
    Optional<WalletTransactionEntity> findLatestBalanceByTimestamp(Long walletId, LocalDateTime timestamp);

    @Query("""
        SELECT t
        FROM WalletTransactionEntity t
        WHERE t.walletId = :walletId 
        ORDER BY t.timestamp DESC LIMIT 1
    """)
    Optional<WalletTransactionEntity> findCurrentBalance(Long walletId);
}
