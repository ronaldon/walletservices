package com.walletservices.domain.usecases;

import com.walletservices.domain.model.Wallet;

public interface CreateWallet {
    Wallet createWallet(String userId, String name);
}
