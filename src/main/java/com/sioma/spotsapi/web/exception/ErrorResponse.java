package com.sioma.spotsapi.web.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class ErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String message;
    private final String code;

    public ErrorResponse(int status, String message, String code) {
        this.timestamp = Instant.now();
        this.status = status;
        this.message = message;
        this.code = code;
    }
}