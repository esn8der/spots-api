package com.sioma.spotsapi.builders;

import com.sioma.spotsapi.domain.model.Usuario;

public class UsuarioBuilder {

    private Long id = 1L;
    private String nombre = "Pedro";
    private String email = "test@mail.com";
    private String password = "123";

    public UsuarioBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public Usuario build() {
        return new Usuario(id, nombre, email, password);
    }
}