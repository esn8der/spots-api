package com.sioma.spotsapi.domain.exception;

public class SpotAlreadyExistsNearbyException extends RuntimeException {
    public SpotAlreadyExistsNearbyException() {
        super("Ya existe un punto muy cercano en el lote");
    }
}
