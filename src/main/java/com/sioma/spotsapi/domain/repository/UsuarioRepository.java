package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Usuario;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsById(Long id);
}
