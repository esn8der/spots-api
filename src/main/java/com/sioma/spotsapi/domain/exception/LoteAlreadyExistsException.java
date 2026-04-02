package com.sioma.spotsapi.domain.exception;

public class LoteAlreadyExistsException extends RuntimeException {
    private final String nombre;
    private final Long fincaId;

    public LoteAlreadyExistsException(String nombre, Long fincaId) {
        super("El lote: " + nombre + " de la finca de id: " + fincaId + " ya existe");
        this.nombre = nombre;
        this.fincaId = fincaId;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getFincaId() {
        return fincaId;
    }

}
