package com.sioma.spotsapi.domain.exception;

public class InvalidGeoSpatialException extends RuntimeException {
    public InvalidGeoSpatialException(String message) {
        super(message);
    }
}