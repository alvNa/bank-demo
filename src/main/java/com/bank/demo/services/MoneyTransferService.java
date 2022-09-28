package com.bank.demo.services;

import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.dto.generic.ResultDto;
import com.bank.demo.exceptions.AccountBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class MoneyTransferService {
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    private final WebClient webClient;

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
}
