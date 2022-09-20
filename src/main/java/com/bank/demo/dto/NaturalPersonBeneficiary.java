package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class NaturalPersonBeneficiary implements Serializable {
    String fiscalCode1;
    String fiscalCode2;
    String fiscalCode3;
    String fiscalCode4;
    String fiscalCode5;
}
