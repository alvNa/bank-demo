package com.bank.demo.services;

import com.bank.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Slf4j
@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
public class AccountService2Test {

    @Value("${server.url}")
    private String serverUrl;

    private MockServerClient mockServerClient;

    @Value("classpath:balance.json")
    private Resource balanceResourceFile;

    @Value("classpath:transactions.json")
    private Resource transactionsResourceFile;

    @Value("classpath:money-transfer-response.json")
    private Resource moneyResponseResourceFile;

    @Test
    void shouldGetBalanceOK() throws IOException {
        val bytes = Files.readAllBytes(balanceResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/accounts/1/balance"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(json));

        AccountService2 accountService = new AccountService2(serverUrl);
        Long accountId = 1L;
        val response = BalanceDto.builder()
                .date(LocalDate.of(2018,8,17))
                .balance(BigDecimal.valueOf(29.64))
                .availableBalance(BigDecimal.valueOf(29.64))
                .currency("EUR")
                .build();

        val maybeBalance = accountService.getBalance(accountId);

        // Assert response
        assertNotNull(maybeBalance.isPresent());
        assertEquals(response, maybeBalance.get());
    }

    @Test
    void shouldGetTransactionsOK() throws IOException {
        val bytes = Files.readAllBytes(transactionsResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/accounts/1/transactions"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(json));

        AccountService2 accountService = new AccountService2(serverUrl);
        Long accountId = 1L;
        val transactionId = 1323L;
        val response = TransactionDto.builder()
                .transactionId(transactionId)
                .build();

        val transactions = accountService.getTransactions(accountId);

        // Assert response
        assertNotNull(transactions);
        //assertEquals(response, maybeBalance.get());
    }

    @Test
    void shouldSendMoneyTransfer() throws IOException {
        val bytes = Files.readAllBytes(moneyResponseResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);

        // Setup request matcher and response using OpenAPI definition
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/accounts/1/payments/money-transfers")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(json)
                );

        AccountService2 accountService = new AccountService2(serverUrl);
        Long accountId = 1L;
        val accountDto = AccountDto.builder()
                .accountCode("IT23A0336844430152923804660")
                .bicCode("SELBIT2BXXX")
                .build();
        val addressDto = AddressDto.builder()
                .city("Madrid")
                .countryCode("34")
                .address("xxx")
                .build();

        val creditor = Creditor.builder()
                .name("John Doe")
                .account(accountDto)
                .address(addressDto)
                .build();

        val req = MoneyTransferRequestDto.builder()
                .creditor(creditor)
                .executionDate(LocalDate.of(2019,4,1))
                .uri("REMITTANCE_INFORMATION")
                .description("Payment invoice 75/2017")
                .build();

        val moneyTransferResponseDto = accountService.sendMoneyTransfer(accountId, req);

        // Assert response
        assertNotNull(moneyTransferResponseDto);
        assertNotNull(moneyTransferResponseDto.getMoneyTransferId());
    }
}