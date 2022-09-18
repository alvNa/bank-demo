package com.bank.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDto implements Serializable {
    private LocalDate date;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
}
