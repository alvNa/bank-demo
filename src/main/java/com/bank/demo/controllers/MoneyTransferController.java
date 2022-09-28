package com.bank.demo.controllers;

import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.services.MoneyTransferService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;

@RequiredArgsConstructor
@RestController
@RequestMapping(ACCOUNT_BASE)
public class MoneyTransferController {
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    private final MoneyTransferService moneyTransferService;

    @SneakyThrows
    @PostMapping(MONEY_TRANSFER_PATH)
    public ResponseEntity<MoneyTransferResponseDto> sendMoneyTransfer(@PathVariable Long accountId, @RequestBody @Valid MoneyTransferRequestDto body) {
        return ResponseEntity.ok(moneyTransferService.sendMoneyTransfer(accountId, body));
    }
}
