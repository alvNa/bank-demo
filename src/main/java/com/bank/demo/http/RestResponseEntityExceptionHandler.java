package com.bank.demo.http;


import com.bank.demo.dto.error.ApiErrorResponse;
import com.bank.demo.exceptions.AccountBusinessException;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Objects;

import static org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = "This should be application specific";
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(NotFoundException e) {
        log.warn("Expected an entity but found none.", e);
        val message = e.getMessage();
        if (Objects.isNull(message) || message.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return new ResponseEntity<>(new ApiErrorResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access if forbidden to the resource", ex);

        return new ResponseEntity<>(new ApiErrorResponse(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccountBusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccountBusinessException ex) {
        log.warn("Business exception", ex);

        return new ResponseEntity<>(new ApiErrorResponse(ex.getErrors()), HttpStatus.NOT_FOUND);
    }



}