package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class BalanceDto implements Serializable {
    private LocalDate date;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
}
