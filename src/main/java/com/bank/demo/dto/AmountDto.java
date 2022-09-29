package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class AmountDto implements Serializable {
    BigDecimal debtorAmount;
    String debtorCurrency;
    BigDecimal creditorAmount;
    String creditorCurrency;
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate creditorCurrencyDate;
    Integer exchangeRate;
}
