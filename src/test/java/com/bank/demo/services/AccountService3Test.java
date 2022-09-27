package com.bank.demo.services;

import com.bank.demo.config.WebClientConfig;
import com.bank.demo.dto.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@Slf4j
@MockServerTest("server.url=http://localhost:${mockServerPort}")
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes= WebClientConfig.class, loader= AnnotationConfigContextLoader.class)
public class AccountService3Test {

    @Value("${server.url}")
    private String serverUrl;

    private MockServerClient mockServerClient;

    @Value("classpath:money-transfer-request1.json")
    private Resource moneyRequestResourceFile;

    @Value("classpath:money-transfer-response2.json")
    private Resource moneyResponseResourceFile;

    @Mock
    private TransactionService transactionService;

    private Account3Service accountService;


    @BeforeAll
    public void beforeAll(){
        accountService = new Account3Service(serverUrl, "FXOVVXXHVCPVPBZXIJOBGUGSKHDNFRRQJP",
                "Europe/Rome",
                "S2S");
    }

    @Test
    void shouldSendMoneyTransferOK() throws IOException {
        val jsonRequest = Files.readString(moneyRequestResourceFile.getFile().toPath());
        val jsonResponse = Files.readString(moneyResponseResourceFile.getFile().toPath());
        val accountId = 14537780L;
        val body = JsonBody.json(jsonRequest);
        mockServerClient
                .when(request()
                        .withMethod("POST")
                        .withPath("/accounts/14537780/payments/money-transfers")
                        .withBody(body)
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
                .build();

        val creditor = Creditor.builder()
                .name("John Doe")
                .account(accountDto)
                .address(addressDto)
                .build();

        val nat = NaturalPersonBeneficiary.builder()
                .fiscalCode1("MRLFNC81L04A859L")
                .build();

        val legal = LegalPersonBeneficiary.builder()
                .build();

        val taxRelief = TaxRelief.builder()
                .taxReliefId("L449")
                .isCondoUpgrade(false)
                .creditorFiscalCode("56258745832")
                .beneficiaryType("NATURAL_PERSON")
                .naturalPersonBeneficiary(nat)
                .legalPersonBeneficiary(legal)
                .build();

        val req = MoneyTransferRequestDto.builder()
                .creditor(creditor)
                .executionDate(LocalDate.of(2019,4,1))
                .uri("REMITTANCE_INFORMATION")
                .description("Payment invoice 75/2017")
                .amount(BigDecimal.valueOf(800))
                .currency("EUR")
                .isUrgent(false)
                .isInstant(false)
                .feeType("SHA")
                .feeAccountId("45685475")
                .taxRelief(taxRelief)
                .build();

        val moneyTransferResponseDto = assertDoesNotThrow(() -> accountService.sendMoneyTransfer(accountId, req));

        // Assert response
        assertNotNull(moneyTransferResponseDto);
        assertNotNull(moneyTransferResponseDto.getMoneyTransferId());
    }
}