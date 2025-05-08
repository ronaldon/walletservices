package com.recargapay.walletservices.adapters.web.controllers.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransactionRequest {
    @NotNull
    private Long walletId;
    @NotNull
    private BigDecimal amount;
}