package com.recargapay.walletservices.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record WalletTransaction(
        Long id,
        Long walletId,
        LocalDateTime timestamp,
        String type,
        BigDecimal amount,
        BigDecimal balance) {
}
