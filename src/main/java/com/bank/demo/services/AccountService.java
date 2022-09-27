package com.bank.demo.services;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.dto.generic.Result2Dto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.AccountBusinessException;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.bank.demo.util.HttpConstants.*;

@Service
public class AccountService {

    private WebClient webClient;

    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";

    public AccountService(@Value("${app.server.url}") String bankSrvUrl,
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

    public Optional<BalanceDto> getBalance(Long accountId) {
        val customer = webClient.get()
                .uri(BALANCE_PATH, accountId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<ResultDto<BalanceDto>>(){});

        return customer.blockOptional()
                .map(ResultDto::getPayload);
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
