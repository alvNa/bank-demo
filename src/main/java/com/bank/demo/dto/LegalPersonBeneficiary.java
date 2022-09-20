package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;


@Data
@Builder
public class LegalPersonBeneficiary implements Serializable {
    String fiscalCode;
    String legalRepresentativeFiscalCode;
}
