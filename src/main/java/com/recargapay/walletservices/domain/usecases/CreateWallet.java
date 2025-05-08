package com.recargapay.walletservices.domain.usecases;

import com.recargapay.walletservices.domain.model.Wallet;

public interface CreateWallet {
    Wallet createWallet(String userId, String name);
}
