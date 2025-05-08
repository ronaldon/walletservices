package com.walletservices.application.port;

import com.walletservices.domain.usecases.DepositFunds;
import com.walletservices.domain.usecases.RetrieveBalance;
import com.walletservices.domain.usecases.TransferFunds;
import com.walletservices.domain.usecases.WithdrawFunds;

public interface WalletTransactionServicePort extends TransferFunds, DepositFunds, WithdrawFunds, RetrieveBalance {

}
