package com.bank.demo.dto.generic;

import lombok.Data;

import java.util.List;

@Data
public class ResultDto<T> {
    String status;
    List<ErrorDto> error;
    T payload;
}
