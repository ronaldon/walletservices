package com.walletservices.domain.usecases;

import java.math.BigDecimal;

public interface TransferFunds {
    void transfer(Long fromWalletId, Long toWalletId, BigDecimal amount);
}
