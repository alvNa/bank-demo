package com.bank.demo.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDto implements Serializable {
    @NonNull
    private LocalDate date;
    @NonNull
    private BigDecimal balance;
    @NonNull
    private BigDecimal availableBalance;
    @NonNull
    private String currency;
}
