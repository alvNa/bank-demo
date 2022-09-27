package com.bank.demo.services;

import com.bank.demo.config.WebClientConfig;
import com.bank.demo.dto.BalanceDto;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.math.BigDecimal;
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
public class AccountServiceTest {

    @Value("${server.url}")
    private String serverUrl;

    private MockServerClient mockServerClient;

    @Value("classpath:balance-response.json")
    private Resource balanceResourceFile;

    private AccountService accountService;


    @BeforeAll
    public void beforeAll(){
        accountService = new AccountService(serverUrl, "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP",
                "Europe/Rome",
                "S2S");
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
}