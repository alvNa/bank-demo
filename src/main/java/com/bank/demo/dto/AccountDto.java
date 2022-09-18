package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder
public class AccountDto implements Serializable {
    String accountCode;
    String bicCode;
}