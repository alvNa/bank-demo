package com.bank.demo.services;

import com.bank.demo.config.WebFluxConfig;
import com.bank.demo.dto.BalanceDto;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;

import static com.bank.demo.services.AccountBalanceService.BALANCE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = WebFluxConfig.class)
public class AccountBalanceServiceTest {

    @Value("${server.url}")
    private String serverUrl;

    @Autowired
    private WebClient webClient;

    private MockServerClient mockServerClient;

    @Value("classpath:balance-response.json")
    private Resource balanceResourceFile;

    private AccountBalanceService accountBalanceService;

    @BeforeAll
    public void beforeAll(){
        accountBalanceService = new AccountBalanceService(webClient);
    }

    @Test
    void shouldGetBalanceOK() throws IOException {
        val jsonResponse = Files.readString(balanceResourceFile.getFile().toPath());

        mockServerClient.when(request()
                        .withMethod("GET")
                        .withPath(BALANCE_PATH)
                        .withPathParameter("accountId","1"))
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

        val maybeBalance = accountBalanceService.getBalance(accountId);

        // Assert response
        assertTrue(maybeBalance.isPresent());
        assertEquals(response, maybeBalance.get());
    }
}