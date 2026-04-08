package com.sioma.spotsapi.fixtures;

import com.sioma.spotsapi.domain.model.Usuario;

public class UsuarioFixtures {
    public static final Long ID = 1L;
    public static final String NOMBRE = "Pedro";
    public static final String EMAIL = "test@mail.com";
    public static final String PASSWORD = "123";
    public static final String PASSWORD_HASHED = "hashedPassword";

    public static Usuario anyUsuario() {
        return new Usuario(ID,
                NOMBRE,
                EMAIL,
                PASSWORD_HASHED
        );
    }
}