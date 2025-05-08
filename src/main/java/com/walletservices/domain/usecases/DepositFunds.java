package com.walletservices.domain.usecases;

import com.walletservices.domain.model.WalletTransaction;

import java.math.BigDecimal;

public interface DepositFunds {
    WalletTransaction deposit(Long walletId, BigDecimal amount);
}
