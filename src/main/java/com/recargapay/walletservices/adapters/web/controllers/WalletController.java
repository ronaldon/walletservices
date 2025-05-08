package com.recargapay.walletservices.adapters.web.controllers;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.recargapay.walletservices.adapters.web.controllers.dto.requests.CreateWalletRequest;
import com.recargapay.walletservices.adapters.web.controllers.dto.response.BalanceResponse;
import com.recargapay.walletservices.application.port.WalletServicesPort;
import com.recargapay.walletservices.application.port.WalletTransactionServicePort;
import com.recargapay.walletservices.domain.model.Wallet;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@RequestMapping("/wallets")
public class WalletController {
    private final WalletServicesPort walletService;
    private  final WalletTransactionServicePort transactionService;

    @GetMapping("/{walletId}/balance")
    public ResponseEntity<BalanceResponse> getCurrentBalance(
            @PathVariable Long walletId,
            @RequestParam(required = false)
            @JsonFormat(pattern="yyyy-MM-dd")
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            LocalDateTime timestamp) {

        BigDecimal balance;
        if (timestamp != null) {
            balance = transactionService.getHistoricalBalance(walletId, timestamp);
        } else {
            balance = transactionService.getBalance(walletId);
        }

        return ResponseEntity.ok(BalanceResponse.builder().balance(balance).build());
    }

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody CreateWalletRequest request) {
        Wallet wallet = walletService.createWallet(request.getUserId(), request.getName());
        return ResponseEntity.created(URI.create(wallet.id().toString())).build();
    }
}