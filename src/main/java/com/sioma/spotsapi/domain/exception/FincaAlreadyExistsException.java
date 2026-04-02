package com.sioma.spotsapi.domain.exception;

public class FincaAlreadyExistsException extends RuntimeException {
    private final String nombre;
    private final Long usuarioId;

    public FincaAlreadyExistsException(String nombre, Long usuarioId) {
        super("La finca: " + nombre + " del usuario de id: " + usuarioId + " ya existe");
        this.nombre = nombre;
        this.usuarioId = usuarioId;
    }

    public String getNombre() {
        return nombre;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }
}
