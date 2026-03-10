package com.sioma.spotsapi.domain.exception;

public class UsuarioAlreadyExistsException extends RuntimeException{

    public UsuarioAlreadyExistsException() {
        super("El usuario con ese correo ya existe");
    }
}
