package com.bank.demo.services;

import com.bank.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountServiceTest {

    @Value("${server.url}")
    private String serverUrl;

    private MockServerClient mockServerClient;

    @Value("classpath:balance.json")
    private Resource balanceResourceFile;

    @Value("classpath:transactions.json")
    private Resource transactionsResourceFile;

    @Value("classpath:money-transfer-response.json")
    private Resource moneyResponseResourceFile;

    private AccountService accountService;

    @BeforeAll
    public void beforeAll(){
        accountService = new AccountService(serverUrl);
    }

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
        val accountId = 14537780L;

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/accounts/14537780/transactions"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(json));

        val transactionId = 1323L;
        val response = TransactionDto.builder()
                .transactionId(transactionId)
                .build();
        val dateFrom = LocalDate.of(2019,04,01);
        val dateTo = LocalDate.of(2019,04,01);

        val transactions = accountService.getTransactions(accountId, dateFrom, dateTo);

        // Assert response
        assertNotNull(transactions);
        //assertEquals(response, maybeBalance.get());
    }

    @Test
    void shouldSendMoneyTransferOK() throws IOException {
        val bytes = Files.readAllBytes(moneyResponseResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);
        val accountId = 14537780L;
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/accounts/14537780/payments/money-transfers")
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(json)
                );

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