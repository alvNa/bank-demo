package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@Builder
public class LegalPersonBeneficiary implements Serializable {
    @NotNull
    String fiscalCode;
    String legalRepresentativeFiscalCode;
}
