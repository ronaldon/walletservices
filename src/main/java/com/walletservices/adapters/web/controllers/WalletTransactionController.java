package com.walletservices.adapters.web.controllers;

import com.walletservices.adapters.web.controllers.dto.requests.TransactionRequest;
import com.walletservices.adapters.web.controllers.dto.requests.TransferRequest;
import com.walletservices.application.port.WalletTransactionServicePort;
import com.walletservices.domain.model.WalletTransaction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/wallets-transactions")
public class WalletTransactionController {

    private final WalletTransactionServicePort transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<Long> deposit(
            @Valid @RequestBody TransactionRequest request) throws InterruptedException {
        var transaction = transactionService.deposit(request.getWalletId(), request.getAmount());
        return ResponseEntity.created(URI.create(transaction.id().toString())).build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Long> withdraw(
            @Valid @RequestBody TransactionRequest request) throws InterruptedException {
        var transaction = transactionService.withdraw(request.getWalletId(), request.getAmount());
        return ResponseEntity.created(URI.create(transaction.id().toString())).build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<WalletTransaction> transfer(
            @Valid @RequestBody TransferRequest request) throws InterruptedException {
        transactionService.transfer(request.getFromWalletId(), request.getToWalletId(), request.getAmount());
        return ResponseEntity.created(URI.create("")).build();
    }
}