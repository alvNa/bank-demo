package com.bank.demo.dto;

import lombok.*;

@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionTypeDto {

    //TYPE_0015 ("GBS_TRANSACTION_TYPE", "GBS_TRANSACTION_TYPE_0015");

    private String enumeration;
    private String value;

    /*TransactionTypeDto(String enumeration, String value) {
        this.enumeration = enumeration;
        this.value = value;
    }*/
}