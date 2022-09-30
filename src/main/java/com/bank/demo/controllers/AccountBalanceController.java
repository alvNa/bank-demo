package com.bank.demo.controllers;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.services.AccountBalanceService;
import com.bank.demo.validation.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;

@RequiredArgsConstructor
@RestController
@RequestMapping(ACCOUNT_BASE)
public class AccountBalanceController {

    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";

    private final AccountBalanceService accountService;

    @GetMapping(BALANCE_PATH)
    public ResponseEntity<BalanceDto> getBalance(@PathVariable @AccountId Long accountId) {
        return accountService.getBalance(accountId).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
