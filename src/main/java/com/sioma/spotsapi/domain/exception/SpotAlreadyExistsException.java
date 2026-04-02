package com.sioma.spotsapi.domain.exception;

public class SpotAlreadyExistsException extends RuntimeException {
    private final Long loteId;
    private final int linea;
    private final int posicion;

    public SpotAlreadyExistsException(Long loteId, int linea, int posicion) {
        super("El spot de la linea: " + linea + " y la posición: " + posicion + " del lote de id: " + loteId + " ya existe");
        this.loteId = loteId;
        this.linea = linea;
        this.posicion = posicion;
    }

    public Long getLoteId() {
        return loteId;
    }

    public int getLinea() {
        return linea;
    }

    public int getPosicion() {
        return posicion;
    }
}
