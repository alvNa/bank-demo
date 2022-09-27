package com.bank.demo.config;

import com.bank.demo.dto.generic.Result2Dto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.AccountBusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.bank.demo.util.HttpConstants.*;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Bean
    public WebClient getWebClient(@Value("${app.server.url}") String bankSrvUrl,
                                  @Value("${app.header.apikey}") String apiKey,
                                  @Value("${app.header.timezone}") String timeZone,
                                  @Value("${app.header.authschema}") String authSchema) {
        return WebClient.builder().baseUrl(bankSrvUrl)
                .defaultHeader(API_KEY_HEADER, apiKey)
                .defaultHeader(TIME_ZONE_HEADER, timeZone)
                .defaultHeader(AUTH_SCHEMA_HEADER, authSchema)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(errorHandler())
                .build();
    }

    private ExchangeFilterFunction errorHandler() {
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