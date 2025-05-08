package com.walletservices.domain.usecases;

import com.walletservices.domain.model.WalletTransaction;

import java.math.BigDecimal;

public interface WithdrawFunds {
    WalletTransaction withdraw(Long walletId, BigDecimal amount);
}
