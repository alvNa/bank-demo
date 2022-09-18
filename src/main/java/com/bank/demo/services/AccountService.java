package com.bank.demo.services;

import com.bank.demo.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class AccountService {
    private String bankSrvUrl;

    private WebClient webClient;

    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";
    public static final String TRANSACTIONS_PATH = "/accounts/{accountId}/transactions";
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    @Autowired
    public AccountService(@Value("${app.bankSrvUrl}") String bankSrvUrl) {
        this.bankSrvUrl = bankSrvUrl;
        this.webClient = WebClient.builder().baseUrl(bankSrvUrl)
                .defaultHeader("Api-Key", "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP")
                .defaultHeader("X-Time-Zone", "Europe/Rome")
                .defaultHeader("Auth-Schema", "S2S")
                .build();
    }

    public Optional<BalanceDto> getBalance(Long accountId) {
        Mono<BalanceDto> customer = webClient.get()
                .uri(BALANCE_PATH, accountId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(BalanceDto.class);

        Optional<BalanceDto> balanceDto = customer.blockOptional();
        return balanceDto;
    }

    public List<TransactionDto> getTransactions(Long accountId, LocalDate from, LocalDate to) {
        Mono<TransactionsDto> transactions = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TRANSACTIONS_PATH)
                        .queryParam("fromAccountingDate", from.toString())
                        .queryParam("toAccountingDate", to.toString())
                        .build(accountId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(TransactionsDto.class);

        Optional<TransactionsDto> transactionsDto = transactions.blockOptional();
        return transactionsDto.map(TransactionsDto::getList).orElse(Collections.emptyList());
    }

    public MoneyTransferResponseDto sendMoneyTransfer(Long accountId, MoneyTransferRequestDto req) {
        Mono<MoneyTransferResponseDto> quotation = webClient.post()
                .uri(MONEY_TRANSFER_PATH, accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), MoneyTransferRequestDto.class)
                .retrieve()
                .bodyToMono(MoneyTransferResponseDto.class);

        return quotation.block();
    }
}
