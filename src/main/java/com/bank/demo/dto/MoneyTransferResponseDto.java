package com.bank.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferResponseDto implements Serializable {
    private Long moneyTransferId;
    private String status;
    private String direction;
    private Creditor creditor;
    private Debtor debtor;
    private String cro;
    private String uri;
    private String trn;
    private String description;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private OffsetDateTime createdDatetime;
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private LocalDateTime accountedDatetime;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate debtorValueDate;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate creditorValueDate;
    private AmountDto amount;
    @JsonProperty("isUrgent")
    boolean isUrgent;
    @JsonProperty("isInstant")
    boolean isInstant;
    private String feeType;
    private String feeAccountId;
    private List<FeeDto> fees;
    @JsonProperty("hasTaxRelief")
    boolean hasTaxRelief;
}