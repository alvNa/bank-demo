package com.bank.demo.services;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.dto.TransactionDto;
import com.bank.demo.dto.generic.PayloadDto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.MoneyTransferExcepion;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

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
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Optional<BalanceDto> getBalance(Long accountId) {
        val customer = webClient.get()
                .uri(BALANCE_PATH, accountId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<ResultDto<BalanceDto>>(){});

        return customer.blockOptional()
                .map(ResultDto::getPayload);
    }

    public List<TransactionDto> getTransactions(Long accountId, LocalDate from, LocalDate to) {
        val result = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TRANSACTIONS_PATH)
                        .queryParam("fromAccountingDate", from.toString())
                        .queryParam("toAccountingDate", to.toString())
                        .build(accountId))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<ResultDto<PayloadDto<TransactionDto>>>(){});

        return result.blockOptional()
                .map(x -> x.getPayload().getList())
                .orElse(Collections.emptyList());
    }

    public MoneyTransferResponseDto sendMoneyTransfer(Long accountId, MoneyTransferRequestDto body) throws MoneyTransferExcepion {
        val transferResult = webClient.post()
                .uri(MONEY_TRANSFER_PATH, accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                //.body(body, MoneyTransferRequestDto.class)
                //.bodyValue(req)
                .body(Mono.just(body), MoneyTransferRequestDto.class)
                //.body(BodyInserters.fromValue(req))
                //.body(BodyInserters.fromValue(req))

                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultDto<MoneyTransferResponseDto>>(){});

        ResultDto<MoneyTransferResponseDto> resultDto = transferResult.block();

        //if (nonNull(resultDto) && nonNull(resultDto.getError()) && !resultDto.getError().isEmpty()){
        if (nonNull(resultDto) && resultDto.getStatus().equals("KO")){
            throw new MoneyTransferExcepion();
        }
        else{
            return resultDto.getPayload();
        }
    }
}
