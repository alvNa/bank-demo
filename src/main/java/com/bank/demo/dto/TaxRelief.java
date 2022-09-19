package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class TaxRelief implements Serializable {
    String taxReliefId;
    String isCondoUpgrade;
    String creditorFiscalCode;
    String beneficiaryType;
    NaturalPersonBeneficiary naturalPersonBeneficiary;
    LegalPersonBeneficiary legalPersonBeneficiary;
}
