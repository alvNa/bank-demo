package com.bank.demo.services;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.dto.TransactionDto;
import com.bank.demo.dto.generic.PayloadDto;
import com.bank.demo.dto.generic.Result2Dto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.AccountBusinessException;
import lombok.AllArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.bank.demo.util.HttpConstants.*;
import static java.util.Objects.nonNull;

@Service
public class AccountService {

    private WebClient webClient;

    @Autowired
    private TransactionService transactionService;

    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";
    public static final String TRANSACTIONS_PATH = "/accounts/{accountId}/transactions";
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    public AccountService(@Value("${app.server.url}") String bankSrvUrl,
                          @Value("${app.header.apikey}") String apiKey,
                          @Value("${app.header.timezone}") String timeZone,
                          @Value("${app.header.authschema}") String authSchema,
                          @Autowired TransactionService transactionService) {

        this.transactionService = transactionService;
        //TODO: Move props and webclient to a WebClient Config
        this.webClient = WebClient.builder().baseUrl(bankSrvUrl)
                .defaultHeader(API_KEY_HEADER, apiKey)
                .defaultHeader(TIME_ZONE_HEADER, timeZone)
                .defaultHeader(AUTH_SCHEMA_HEADER, authSchema)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorHandler())
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

        val transactionDtos = result.blockOptional()
                .map(x -> x.getPayload().getList())
                .orElse(Collections.emptyList());
        transactionService.saveAll(transactionDtos);
        return transactionDtos;
    }

    public MoneyTransferResponseDto sendMoneyTransfer(Long accountId, MoneyTransferRequestDto body) throws AccountBusinessException {
        val transferResult = webClient.post()
                .uri(MONEY_TRANSFER_PATH, accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), MoneyTransferRequestDto.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ResultDto<MoneyTransferResponseDto>>(){});

        ResultDto<MoneyTransferResponseDto> resultDto = transferResult.block();

        if (nonNull(resultDto) && resultDto.getStatus().equals("KO")){
            throw new AccountBusinessException();
        }
        else{
            return Objects.requireNonNull(resultDto).getPayload();
        }
    }

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse
                        .bodyToMono(new ParameterizedTypeReference<ResultDto<?>>(){})
                        .flatMap(errorBody ->
                                Mono.error(new AccountBusinessException(errorBody.getError())));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse
                        .bodyToMono(new ParameterizedTypeReference<Result2Dto<?>>(){})
                        .flatMap(errorBody ->
                                Mono.error(new AccountBusinessException(errorBody.getErrors())));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
