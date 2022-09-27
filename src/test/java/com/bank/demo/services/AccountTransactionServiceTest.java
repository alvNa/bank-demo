package com.bank.demo.services;

import com.bank.demo.config.WebFluxConfig;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.bank.demo.services.AccountTransactionService.TRANSACTIONS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = WebFluxConfig.class)
public class AccountTransactionServiceTest {

    @Value("${server.url}")
    private String serverUrl;

    @Autowired
    private WebClient webClient;

    private MockServerClient mockServerClient;

    @Value("classpath:transactions-response.json")
    private Resource transactionsResourceFile;

    @Mock
    private TransactionService transactionService;

    private AccountTransactionService accountService;

    @BeforeAll
    public void beforeAll(){
        accountService = new AccountTransactionService(webClient,transactionService);
    }

    @Test
    void shouldGetTransactionsOK() throws IOException {
        val jsonResponse = Files.readString(transactionsResourceFile.getFile().toPath());
        Long accountId = 14537780L;
        val dateFrom = LocalDate.of(2016,12,01);
        val dateTo = LocalDate.of(2017,01,01);

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath(TRANSACTIONS_PATH)
                        .withPathParameter("accountId",accountId.toString())
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