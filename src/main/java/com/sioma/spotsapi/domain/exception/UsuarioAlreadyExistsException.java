package com.sioma.spotsapi.domain.exception;

public class UsuarioAlreadyExistsException extends RuntimeException {

    private final String email;

    public UsuarioAlreadyExistsException(String email) {
        super("El usuario con el correo: " + email + " ya existe");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
