package com.bank.demo.services;

import com.bank.demo.dto.TransactionDto;
import com.bank.demo.dto.generic.PayloadDto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.validation.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountTransactionService {
    public static final String TRANSACTIONS_PATH = "/accounts/{accountId}/transactions";

    private final WebClient webClient;
    private final TransactionService transactionService;

    private final AccountValidator accountValidator = new AccountValidator();

    public List<TransactionDto> getTransactions(Long accountId, LocalDate from, LocalDate to) {
        accountValidator.validate(accountId);

        val result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TRANSACTIONS_PATH)
                        .queryParam("fromAccountingDate", from.toString())
                        .queryParam("toAccountingDate", to.toString())
                        .build(accountId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<ResultDto<PayloadDto<TransactionDto>>>(){});

        val transactionDtos = result.blockOptional()
                .map(x -> x.getPayload().getList())
                .orElse(Collections.emptyList());
        transactionService.saveAll(transactionDtos);
        return transactionDtos;
    }
}
