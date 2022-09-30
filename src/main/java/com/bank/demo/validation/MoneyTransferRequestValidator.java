package com.bank.demo.validation;

import com.bank.demo.dto.MoneyTransferRequestDto;
import lombok.val;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;


public class MoneyTransferRequestValidator implements DtoValidator<MoneyTransferRequestDto>{
    public static final String INVALID_MONEY_TRANSFER_REQUEST = "Invalid MoneyTransferReques";

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public void validate(MoneyTransferRequestDto dto) {
        val errors = validator.validate(dto);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(INVALID_MONEY_TRANSFER_REQUEST, errors);
        }
    }
}

