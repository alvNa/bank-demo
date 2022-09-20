package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaxRelief implements Serializable {
    String taxReliefId;
    @JsonProperty("isCondoUpgrade")
    boolean isCondoUpgrade;
    String creditorFiscalCode;
    String beneficiaryType;
    NaturalPersonBeneficiary naturalPersonBeneficiary;
    LegalPersonBeneficiary legalPersonBeneficiary;
}
