package com.recargapay.walletservices.application.port;

import com.recargapay.walletservices.domain.usecases.DepositFunds;
import com.recargapay.walletservices.domain.usecases.RetrieveBalance;
import com.recargapay.walletservices.domain.usecases.TransferFunds;
import com.recargapay.walletservices.domain.usecases.WithdrawFunds;

public interface WalletTransactionServicePort extends TransferFunds, DepositFunds, WithdrawFunds, RetrieveBalance {

}
