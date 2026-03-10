package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario){
        UsuarioEntity entity = new UsuarioEntity(usuario.getNombre(), usuario.getEmail(), usuario.getPassword());

        entity = jpaRepository.save(entity);

        return new Usuario(entity.getId(), entity.getNombre(), entity.getEmail(), entity.getPassword());
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }
}
