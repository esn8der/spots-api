package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateUsuarioUseCase {
    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario execute(String nombre, String email, String password) {
        log.debug("Creando usuario con nombre: {}, email: {}, password: {}", nombre, email, password);

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new UsuarioAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(password);
        Usuario usuario = new Usuario(nombre, email, passwordHash);

        log.info("Usuario creado exitosamente con nombre: {}", nombre);
        return repository.save(usuario);
    }
}
