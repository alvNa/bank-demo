package com.bank.demo.validation;

public interface DtoValidator<DTO> {

    void validate(DTO dto);
}
