package com.bank.demo.services;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.dto.generic.ResultDto;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountBalanceService {
    public static final String BALANCE_PATH = "/accounts/{accountId}/balance";

    private final WebClient webClient;

    public Optional<BalanceDto> getBalance(Long accountId) {
        val customer = webClient.get()
                .uri(BALANCE_PATH, accountId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                .bodyToMono(new ParameterizedTypeReference<ResultDto<BalanceDto>>(){});

        return customer.blockOptional()
                .map(ResultDto::getPayload);
    }
}
