package com.bank.demo.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.nonNull;

public class AccountIdConstraintValidator implements ConstraintValidator<AccountId, Long> {

    @Override
    public void initialize(AccountId constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long acccountId, ConstraintValidatorContext ctx) {
        return nonNull(acccountId) && acccountId>0;
    }
}