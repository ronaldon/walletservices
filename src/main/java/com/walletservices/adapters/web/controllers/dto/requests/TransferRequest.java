package com.walletservices.adapters.web.controllers.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequest {
    @NotNull
    private Long fromWalletId;
    @NotNull
    private Long toWalletId;
    @NotNull
    private BigDecimal amount;
}