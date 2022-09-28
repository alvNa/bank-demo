package com.bank.demo.controllers;

import com.bank.demo.dto.AccountDto;
import com.bank.demo.dto.Creditor;
import com.bank.demo.dto.MoneyTransferRequestDto;
import com.bank.demo.dto.MoneyTransferResponseDto;
import com.bank.demo.http.RestResponseEntityExceptionHandler;
import com.bank.demo.services.MoneyTransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.bank.demo.controllers.MoneyTransferController.MONEY_TRANSFER_PATH;
import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({MoneyTransferController.class})
@Import(RestResponseEntityExceptionHandler.class)
public class MoneyTransferControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MoneyTransferService moneyTransferService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSendMoneyTransferWithNoBodyKO() throws Exception {
        Long accountId = -1L;

        mockMvc.perform(post(ACCOUNT_BASE + MONEY_TRANSFER_PATH, accountId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldSendMoneyTransferWithInvalidBodyKO() throws Exception {
        Long accountId = -1L;

        val req = MoneyTransferRequestDto.builder()
                .uri("uri")
                .description("desc")
                .build();

        mockMvc.perform(post(ACCOUNT_BASE + MONEY_TRANSFER_PATH, accountId)
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldSendMoneyTransferOK() throws Exception {
        Long accountId = -1L;
        val account = AccountDto.builder()
                .accountCode("IT12345")
                .bicCode("sdf")
                .build();
        val creditor = Creditor.builder()
                .account(account)
                .name("Jhon")
                .build();
        val req = MoneyTransferRequestDto.builder()
                .creditor(creditor)
                .amount(BigDecimal.valueOf(11L))
                .currency("EUR")
                .build();

        val res = MoneyTransferResponseDto.builder()
                .moneyTransferId(1L)
                .direction("XXX")
                .build();

        when(moneyTransferService.sendMoneyTransfer(accountId, req)).thenReturn(res);

        mockMvc.perform(post(ACCOUNT_BASE + MONEY_TRANSFER_PATH, accountId)
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.moneyTransferId").value(1L))
                .andExpect(jsonPath("$.status").isEmpty())
                .andExpect(jsonPath("$.direction").value("XXX"));
    }
}
