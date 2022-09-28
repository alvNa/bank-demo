package com.bank.demo.controllers;

import com.bank.demo.dto.TransactionDto;
import com.bank.demo.services.AccountTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;

@RequiredArgsConstructor
@RestController
@RequestMapping(ACCOUNT_BASE)
public class TransactionController {
    public static final String TRANSACTIONS_PATH = "/accounts/{accountId}/transactions";
    public static final String FROM_DATE_QUERY_PARAM = "fromAccountingDate";
    public static final String TO_DATE_QUERY_PARAM = "toAccountingDate";

    private final AccountTransactionService accountTransactionService;

    @GetMapping(TRANSACTIONS_PATH)
    public ResponseEntity<List<TransactionDto>> getTransactions(@PathVariable Long accountId,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                @RequestParam(FROM_DATE_QUERY_PARAM) LocalDate fromDate,
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                                @RequestParam(TO_DATE_QUERY_PARAM) LocalDate toDate) {
        return ResponseEntity.ok(accountTransactionService.getTransactions(accountId, fromDate, toDate));
    }
}
