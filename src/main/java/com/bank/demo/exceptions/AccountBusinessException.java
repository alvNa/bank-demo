package com.bank.demo.exceptions;

import com.bank.demo.dto.generic.ErrorDto;
import lombok.Data;

import java.util.List;

@Data
public class AccountBusinessException extends Exception {
    private List<ErrorDto> errors;

    public AccountBusinessException() {
        super();
    }

    public AccountBusinessException(List<ErrorDto> errors) {
        super();
        this.errors = errors;
    }
}
