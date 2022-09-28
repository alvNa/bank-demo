package com.bank.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferResponseDto implements Serializable {
    private Long moneyTransferId;
    private String status;
    private String direction;
    private Creditor creditor;
    private Debtor debtor;
}