package com.bank.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Creditor implements Serializable {
    @NotNull
    String name;
    @NotNull
    AccountDto account;
    AddressDto address;
}
