package com.bank.demo.services;

import com.bank.demo.dto.*;
import com.bank.demo.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AccountService {
    public static final String BASE_URL = "https://sandbox.platfr.io/api/gbs/banking/v4.0";
    public static final String ACCOUNTS_PATH = "/accounts";
    public static final String BALANCE_PATH = "/accounts/%s/balance";
    public static final String TRANSACTIONS_PATH = "/accounts/%s/transactions";
    public static final String MONEY_TRANSFER_PATH = "/accounts/{accountId}/payments/money-transfers";

    @Autowired
    private RestTemplate restTemplate;

    public Optional<BalanceDto> getBalance(Long accountId) {
        String uri = String.format(BASE_URL + BALANCE_PATH, accountId);
        try {
            val balanceDto = get(uri,BalanceDto.class);
            return Optional.ofNullable(balanceDto);
        }
        catch (HttpClientErrorException ex){
            return Optional.empty();
        }
    }

    public List<TransactionDto> getTransactions(Long accountId) {
        String uri = String.format(BASE_URL + TRANSACTIONS_PATH, accountId);
        try {
            val transactionsDto = get(uri, TransactionsDto.class);
            return transactionsDto.getList();
        }
        catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            return Collections.emptyList();
        }
    }

    public Optional<MoneyTransferResponseDto> commitTransfer(Long accountId, MoneyTransferRequestDto body) {
        String uri = String.format(BASE_URL + TRANSACTIONS_PATH, accountId);
        try {
            val moneyTransferResponseDto = save(uri, body, MoneyTransferResponseDto.class);
            return Optional.of(moneyTransferResponseDto);
        }
        catch (HttpClientErrorException ex){
            log.error(ex.getMessage());
            return Optional.empty();
        }
    }

    private <T> T get(String uri, Class<T> clazz) {
        return exchange(uri, HttpMethod.GET, clazz);
    }

    private <T> T save(String uri, Object body, Class<T> clazz) {
        val response = restTemplate.postForEntity(uri, body, clazz);
        return response.getBody();
    }

    private <T> T exchange(String uri, HttpMethod httpMethod, Class<T> clazz) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Api-Key", "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP");
        headers.set("X-Time-Zone", "Europe/Rome");
        headers.set("Auth-Schema", "S2S");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<T> resp = restTemplate.exchange(uri, httpMethod, entity, clazz);
        if (resp.getStatusCode().equals(HttpStatus.OK)) {
            return resp.getBody();
        }
        throw new NotFoundException(String.format("Failed to retrieve data (response status %d)", resp.getStatusCodeValue()));
    }

}
