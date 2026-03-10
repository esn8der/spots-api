package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import org.springframework.stereotype.Service;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;

@Service
public class CreateUsuarioUseCase {
    private final UsuarioRepository repository;

    public CreateUsuarioUseCase(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario execute(String nombre, String email, String password) {

        if(repository.existsByEmailIgnoreCase(email)) {
            throw new UsuarioAlreadyExistsException();
        }

        Usuario usuario = new Usuario(nombre, email, password);

        return repository.save(usuario);
    }
}
