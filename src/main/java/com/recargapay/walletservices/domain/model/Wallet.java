package com.recargapay.walletservices.domain.model;

import lombok.Builder;

@Builder
public record Wallet(String id, String userId, String name) { }
