package com.bank.demo.services;

import com.bank.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Assertions;
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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
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

    @Value("classpath:balance-response.json")
    private Resource balanceResourceFile;

    @Value("classpath:transactions-response.json")
    private Resource transactionsResourceFile;

    @Value("classpath:money-transfer-request1.json")
    private Resource moneyRequestResourceFile;

    @Value("classpath:money-transfer-response2.json")
    private Resource moneyResponseResourceFile;

    private AccountService accountService;

    @BeforeAll
    public void beforeAll(){
        accountService = new AccountService(serverUrl);
    }

    @Test
    void shouldGetBalanceOK() throws IOException {
        val jsonResponse = Files.readString(balanceResourceFile.getFile().toPath());

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/accounts/1/balance"))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(jsonResponse));

        Long accountId = 1L;
        val response = BalanceDto.builder()
                .date(LocalDate.of(2022,9,20))
                .balance(BigDecimal.valueOf(7.27))
                .availableBalance(BigDecimal.valueOf(7.27))
                .currency("EUR")
                .build();

        val maybeBalance = accountService.getBalance(accountId);

        // Assert response
        assertNotNull(maybeBalance.isPresent());
        assertEquals(response, maybeBalance.get());
    }

    @Test
    void shouldGetTransactionsOK() throws IOException {
        val jsonResponse = Files.readString(transactionsResourceFile.getFile().toPath());
        val accountId = 14537780L;
        val dateFrom = LocalDate.of(2016,12,01);
        val dateTo = LocalDate.of(2017,01,01);

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath("/accounts/14537780/transactions")
                        .withQueryStringParameter("fromAccountingDate",dateFrom.toString())
                        .withQueryStringParameter("toAccountingDate",dateTo.toString()))
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(jsonResponse));

        val transactionId = 1001049464001L;

        val transactions = accountService.getTransactions(accountId, dateFrom, dateTo);

        assertNotNull(transactions);
        assertEquals(6, transactions.size());
        assertEquals(transactionId, transactions.get(0).getTransactionId());
    }

    @Test
    void shouldSendMoneyTransferOK() throws IOException {
        val jsonRequest = Files.readString(moneyRequestResourceFile.getFile().toPath());
        val jsonResponse = Files.readString(moneyResponseResourceFile.getFile().toPath());
        val accountId = 14537780L;
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/accounts/14537780/payments/money-transfers")
                        .withBody(jsonRequest)
                )
                .respond(response()
                        .withStatusCode(200)
                        .withContentType(MediaType.APPLICATION_JSON)
                        .withBody(jsonResponse)
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
                //.creditor(creditor)
                .executionDate(LocalDate.of(2019,4,1))
                .uri("REMITTANCE_INFORMATION")
                .description("Payment invoice 75/2017")
                .build();

        val moneyTransferResponseDto = assertDoesNotThrow(() -> accountService.sendMoneyTransfer(accountId, req));

        // Assert response
        assertNotNull(moneyTransferResponseDto);
        assertNotNull(moneyTransferResponseDto.getMoneyTransferId());
    }
}