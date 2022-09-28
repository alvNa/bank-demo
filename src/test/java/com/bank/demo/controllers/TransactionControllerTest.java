package com.bank.demo.controllers;

import com.bank.demo.dto.TransactionDto;
import com.bank.demo.http.RestResponseEntityExceptionHandler;
import com.bank.demo.services.AccountTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static com.bank.demo.controllers.TransactionController.*;
import static com.bank.demo.util.HttpConstants.ACCOUNT_BASE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TransactionController.class})
@Import(RestResponseEntityExceptionHandler.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountTransactionService accountTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetTransactions() throws Exception {
        Long accountId = 1L;
        val t1 = TransactionDto.builder().transactionId(1L).build();
        val t2 = TransactionDto.builder().transactionId(2L).build();
        val dateFrom = LocalDate.of(2019,04,01);
        val dateTo = LocalDate.of(2019,04,01);

        when(accountTransactionService.getTransactions(accountId, dateFrom, dateTo)).thenReturn(Arrays.asList(t1,t2));

        mockMvc.perform(get(ACCOUNT_BASE + TRANSACTIONS_PATH, accountId)
                        .param(FROM_DATE_QUERY_PARAM, dateFrom.toString())
                        .param(TO_DATE_QUERY_PARAM, dateTo.toString())
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
}
