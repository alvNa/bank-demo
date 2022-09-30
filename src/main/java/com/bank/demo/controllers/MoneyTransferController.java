package com.bank.demo.controllers;

import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.services.MoneyTransferService;
import com.bank.demo.validation.AccountId;
import com.bank.demo.validation.TimeZoneFormat;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;
import static com.bank.demo.util.HttpConstants.TIME_ZONE_HEADER;

@RequiredArgsConstructor
@RestController
@RequestMapping(ACCOUNT_BASE)
@Validated
public class MoneyTransferController {
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    private final MoneyTransferService moneyTransferService;

    @SneakyThrows
    @PostMapping(MONEY_TRANSFER_PATH)
    public ResponseEntity<MoneyTransferResponseDto> sendMoneyTransfer(
            @RequestHeader(TIME_ZONE_HEADER) @NotNull @TimeZoneFormat String timeZoneHeader,
            @PathVariable @AccountId Long accountId,
            @Valid @RequestBody MoneyTransferRequestDto body) {

        return ResponseEntity.ok(moneyTransferService.sendMoneyTransfer(accountId, body));
    }
}
