package com.sioma.spotsapi.web.exception;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Collections;

@Getter
@JsonPropertyOrder({"status", "message", "code", "errors", "timestamp"})
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String message;
    private final String code;
    private final List<FieldError> errors;

    // Constructor para errores simples
    public ErrorResponse(int status, String message, String code) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.code = code;
        this.errors = Collections.emptyList();
    }

    // Constructor para errores de validación (con detalles de campos)
    public ErrorResponse(int status, String message, String code, List<FieldError> errors) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.code = code;
        this.errors = errors != null ? errors : Collections.emptyList();
    }

    public record FieldError(String field, String message) {
    }
}