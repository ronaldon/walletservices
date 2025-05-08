package com.recargapay.walletservices.adapters.web.controllers.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private String message;
    private String errorCode;

    public ErrorResponse(String message, String errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }
}
