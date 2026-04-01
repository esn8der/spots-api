package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepository {
    void deleteById(Long id);
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    boolean existsByEmailIgnoreCase(String email);
}
