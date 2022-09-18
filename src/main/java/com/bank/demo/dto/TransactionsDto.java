package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
public class TransactionsDto implements Serializable {
    List<TransactionDto> list;
}