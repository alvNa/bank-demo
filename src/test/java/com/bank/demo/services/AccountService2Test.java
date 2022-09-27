package com.bank.demo.services;

import com.bank.demo.config.WebClientConfig;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
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
@ContextConfiguration(classes= WebClientConfig.class, loader= AnnotationConfigContextLoader.class)
public class AccountService2Test {

    @Value("${server.url}")
    private String serverUrl;

    private MockServerClient mockServerClient;

    @Value("classpath:transactions-response.json")
    private Resource transactionsResourceFile;

    @Mock
    private TransactionService transactionService;

    private Account2Service accountService;


    @BeforeAll
    public void beforeAll(){
        accountService = new Account2Service(serverUrl, "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP",
                "Europe/Rome",
                "S2S",
                transactionService);
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
}