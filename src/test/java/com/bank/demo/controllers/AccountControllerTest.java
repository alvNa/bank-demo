package com.bank.demo.controllers;

import com.bank.demo.dto.BalanceDto;
import com.bank.demo.http.RestResponseEntityExceptionHandler;
import com.bank.demo.services.AccountService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.bank.demo.controllers.AccountController.ACCOUNT_BASE;
import static com.bank.demo.controllers.AccountController.BALANCE_PATH;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AccountController.class})
@Import(RestResponseEntityExceptionHandler.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

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
}
