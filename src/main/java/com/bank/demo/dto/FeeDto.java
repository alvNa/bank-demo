package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FeeDto implements Serializable {
    private String feeCode;
    private String description;
    private BigDecimal amount;
    private String currency;
}
