package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    private LocalDate executionDate;
    private String uri;
    private String description;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    @NotBlank
    private String currency;
    private boolean isUrgent;
    private boolean isInstant;
    private String feeType;
    private Long feeAccountId;
}
