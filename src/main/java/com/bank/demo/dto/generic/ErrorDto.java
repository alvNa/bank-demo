package com.bank.demo.dto.generic;

import lombok.Data;

@Data
public class ErrorDto {
    String code;
    String description;
    String params;
}
