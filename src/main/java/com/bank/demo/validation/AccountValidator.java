package com.bank.demo.validation;

import javax.validation.ConstraintViolationException;
import java.util.Collections;

import static java.util.Objects.isNull;

public class AccountValidator implements DtoValidator<Long>{

    public static final String NULL_ACCOUNT_ID = "Null accountId";
    public static final String NEGATIVE_ACCOUNT_ID = "Negative accountId";

    @Override
    public void validate(Long accountId) {
        if (isNull(accountId)){
            throw new ConstraintViolationException(NULL_ACCOUNT_ID, Collections.emptySet());
        }
        else if(accountId<0){
            throw new ConstraintViolationException(NEGATIVE_ACCOUNT_ID, Collections.emptySet());
        }
    }
}