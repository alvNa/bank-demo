package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoneyTransferRequestDto implements Serializable {
    @NotNull
    private Creditor creditor;
    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate executionDate;
    String uri;
    @NotNull
    String description;
    @NotNull
    BigDecimal amount;
    @NotNull
    @NotBlank
    String currency;
    @JsonProperty("isUrgent")
    boolean isUrgent;
    @JsonProperty("isInstant")
    boolean isInstant;
    String feeType;
    String feeAccountId;
    private TaxRelief taxRelief;
}
