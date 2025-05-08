package com.walletservices.adapters.web.controllers.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateWalletRequest {
    @NotNull
    private String userId;
    @NotNull
    private String name;
}