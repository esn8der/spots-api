package com.sioma.spotsapi.domain.exception;

public class PointOutsideLoteException extends RuntimeException {
    public PointOutsideLoteException() {
        super("El punto no pertenece a la geocerca del lote");
    }
}
