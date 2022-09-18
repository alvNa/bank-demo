package com.bank.demo.dto.error;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
public class ApiErrorResponse {
    private final LocalDateTime timestamp;
    private final Integer status;
    private final String message;

    public ApiErrorResponse(Integer status, String message) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
    }
}