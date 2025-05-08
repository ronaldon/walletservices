package com.recargapay.walletservices.adapters.web.controllers.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class BalanceResponse {
    private BigDecimal balance;
}