package com.sioma.spotsapi.infrastructure.geospatial.exception;

public class InvalidGeoSpatialException extends RuntimeException {
    public InvalidGeoSpatialException(String message) {
        super(message);
    }
}