package com.bank.demo.services;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.bank.demo.services.AccountService.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(AccountService.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@ContextConfiguration
public class AccountServiceTest {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private AccountService accountService;

    @Value("classpath:balance.json")
    private Resource balanceResourceFile;

    @Value("classpath:transactions.json")
    private Resource transactionsResourceFile;

    @Test
    public void shouldGetBalanceOk() throws URISyntaxException, IOException {
        val accountId = 14537780L;
        val bytes = Files.readAllBytes(balanceResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);
        String uri = String.format(BASE_URL + BALANCE_PATH, accountId);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(uri)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json)
                );

        val maybeBalanceDto = accountService.getBalance(accountId);
        mockServer.verify();
        Assertions.assertTrue(maybeBalanceDto.isPresent());
    }

    @Test
    public void shouldGetTransactionsOk() throws URISyntaxException, IOException {
        val accountId = 14537780L;
        val bytes = Files.readAllBytes(transactionsResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);
        String uri = String.format(BASE_URL + TRANSACTIONS_PATH, accountId);

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(uri)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json)
                );

        val transactions = accountService.getTransactions(accountId);
        mockServer.verify();
        Assertions.assertFalse(transactions.isEmpty());
    }

    @Test
    public void shouldCommitTransactionOk() throws URISyntaxException, IOException {
        val accountId = 14537780L;
        val bytes = Files.readAllBytes(transactionsResourceFile.getFile().toPath());
        String json = new String(bytes, StandardCharsets.UTF_8);
        String uri = String.format(BASE_URL + TRANSACTIONS_PATH, accountId);

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(uri)))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(json)
                );

        val transactions = accountService.commitTransfer(accountId, null);
        mockServer.verify();
        Assertions.assertFalse(transactions.isEmpty());
    }
}