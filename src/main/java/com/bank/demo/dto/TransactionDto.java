package com.bank.demo.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto implements Serializable {
    private Long transactionId;
    private Long operationId;
    private LocalDate accountingDate;
    private LocalDate valueDate;
    private TransactionTypeDto type;
    private BigDecimal amount;
    private String description;
}
