package com.bank.demo.services;

import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.dto.generic.Result2Dto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.AccountBusinessException;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.bank.demo.util.HttpConstants.*;
import static java.util.Objects.nonNull;

@Service
public class Account3Service {

    private WebClient webClient;

    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    public Account3Service(@Value("${app.server.url}") String bankSrvUrl,
                           @Value("${app.header.apikey}") String apiKey,
                           @Value("${app.header.timezone}") String timeZone,
                           @Value("${app.header.authschema}") String authSchema) {

        //TODO: Move props and webclient to a WebClient Config
        this.webClient = WebClient.builder().baseUrl(bankSrvUrl)
                .defaultHeader(API_KEY_HEADER, apiKey)
                .defaultHeader(TIME_ZONE_HEADER, timeZone)
                .defaultHeader(AUTH_SCHEMA_HEADER, authSchema)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorHandler())
                .build();
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
