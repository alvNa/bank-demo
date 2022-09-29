package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
public class NaturalPersonBeneficiary implements Serializable {
    @NotNull
    String fiscalCode1;
    String fiscalCode2;
    String fiscalCode3;
    String fiscalCode4;
    String fiscalCode5;
}
