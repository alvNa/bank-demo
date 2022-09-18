package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Creditor implements Serializable {
    private String name;
    private AccountDto account;
    private AddressDto address;
}
