package com.bank.demo.dto.generic;

import lombok.Data;

import java.util.List;

/**
 * Some responses in the API return errors instead error
 * */
@Data
public class Result2Dto<T> {
    String status;
    List<ErrorDto> errors;
    T payload;
}
