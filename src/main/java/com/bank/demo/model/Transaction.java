package com.bank.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "TRANSACTIONS")
public class Transaction {
    @Id
    @Column(name = "ID")
    private Long transactionId;

    @Column(name = "OPERATION_ID")
    private Long operationId;

    @Column(name = "ACCOUNTING_DATE")
    private LocalDate accountingDate;

    @Column(name = "VALUE_DATE")
    private LocalDate valueDate;

    @ManyToOne
    @JoinColumn(name = "TRANSACTION_TYPE")
    private TransactionType transactionType;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "DESCRIPTION")
    private String description;
}
