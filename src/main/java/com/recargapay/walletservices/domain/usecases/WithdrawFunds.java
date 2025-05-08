package com.recargapay.walletservices.domain.usecases;

import com.recargapay.walletservices.domain.model.WalletTransaction;

import java.math.BigDecimal;

public interface WithdrawFunds {
    WalletTransaction withdraw(Long walletId, BigDecimal amount);
}
