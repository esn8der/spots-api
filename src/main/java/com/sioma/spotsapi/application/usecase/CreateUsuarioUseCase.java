package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUsuarioUseCase {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CreateUsuarioUseCase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario execute(String nombre, String email, String password) {

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new UsuarioAlreadyExistsException();
        }

        String passwordHash = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(nombre, email, passwordHash);

        return repository.save(usuario);
    }
}
