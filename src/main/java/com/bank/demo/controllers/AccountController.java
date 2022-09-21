package com.bank.demo.controllers;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.dto.TransactionDto;
import com.bank.demo.services.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

import static com.bank.demo.controllers.AccountController.ACCOUNT_BASE;

@RequiredArgsConstructor
@RestController
@RequestMapping(ACCOUNT_BASE)
public class AccountController {
    public static final String ACCOUNT_BASE = "/api/gbs/banking/v4.0";
    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";
    public static final String TRANSACTIONS_PATH = "/accounts/{accountId}/transactions";
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    private final AccountService accountService;


    @GetMapping(BALANCE_PATH)
    public ResponseEntity<BalanceDto> getBalance(@PathVariable Long accountId) {
        return accountService.getBalance(accountId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(TRANSACTIONS_PATH)
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long accountId,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                @RequestParam("fromAccountingDate") LocalDate fromDate,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                @RequestParam("toAccountingDate") LocalDate toDate) {
        return ResponseEntity.ok(accountService.getTransactions(accountId, fromDate, toDate));
    }

    @SneakyThrows
    @PostMapping(MONEY_TRANSFER_PATH)
    public ResponseEntity<MoneyTransferResponseDto> sendMoneyTransfer(@PathVariable Long accountId, @RequestBody @Valid MoneyTransferRequestDto body) {
        return ResponseEntity.ok(accountService.sendMoneyTransfer(accountId, body));
    }
}
