package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Creditor implements Serializable {
    String name;
    AccountDto account;
    AddressDto address;
}
