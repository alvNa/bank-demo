package com.bank.demo.controllers;

import com.bank.demo.dto.*;
import com.bank.demo.http.RestResponseEntityExceptionHandler;
import com.bank.demo.services.AccountService;
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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.bank.demo.controllers.AccountController.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AccountController.class})
@Import(RestResponseEntityExceptionHandler.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetBalance() throws Exception {
        Long accountId = 1L;
        val balanceDto = BalanceDto.builder()
                .date(LocalDate.now())
                .balance(BigDecimal.valueOf(29.64))
                .availableBalance(BigDecimal.valueOf(29.64))
                .build();

        when(accountService.getBalance(accountId)).thenReturn(Optional.of(balanceDto));

        mockMvc.perform(get(ACCOUNT_BASE + BALANCE_PATH, accountId))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundBalanceWhenInvalidAccountId() throws Exception {
        Long accountId = -1L;

        when(accountService.getBalance(accountId)).thenReturn(Optional.empty());

        mockMvc.perform(get(ACCOUNT_BASE + BALANCE_PATH, accountId))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldGetTransactions() throws Exception {
        Long accountId = 1L;
        val t1 = TransactionDto.builder().transactionId(1L).build();
        val t2 = TransactionDto.builder().transactionId(2L).build();
        val dateFrom = LocalDate.of(2019,04,01);
        val dateTo = LocalDate.of(2019,04,01);

        when(accountService.getTransactions(accountId, dateFrom, dateTo)).thenReturn(Arrays.asList(t1,t2));

        mockMvc.perform(get(ACCOUNT_BASE + TRANSACTIONS_PATH, accountId)
                        .param("fromAccountingDate", dateFrom.toString())
                        .param("toAccountingDate", dateTo.toString())
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.*").isArray());
    }

    @Test
    void shouldFailGetTransactionsWhenBadRequest() throws Exception {
        val accountId = 14537780L;
        mockMvc.perform(get(ACCOUNT_BASE + TRANSACTIONS_PATH, accountId))
                .andExpect(status().is4xxClientError());
    }

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

        when(accountService.sendMoneyTransfer(accountId, req)).thenReturn(res);

        mockMvc.perform(post(ACCOUNT_BASE + MONEY_TRANSFER_PATH, accountId)
                        .content(objectMapper.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.moneyTransferId").value(1L))
                .andExpect(jsonPath("$.status").isEmpty())
                .andExpect(jsonPath("$.direction").value("XXX"));
    }
}
