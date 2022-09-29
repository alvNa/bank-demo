package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaxRelief implements Serializable {
    String taxReliefId;
    @NotNull
    @JsonProperty("isCondoUpgrade")
    boolean isCondoUpgrade;
    @NotNull
    String creditorFiscalCode;
    @NotNull
    String beneficiaryType;
    NaturalPersonBeneficiary naturalPersonBeneficiary;
    LegalPersonBeneficiary legalPersonBeneficiary;
}
