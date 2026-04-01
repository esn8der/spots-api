package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.UsuarioEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    @Override
    public Usuario save(Usuario usuario) {
        log.debug("Guardando usuario: {}", usuario.getNombre());

        UsuarioEntity entity = new UsuarioEntity(
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getPassword()
        );
        entity = jpaRepository.save(entity);

        log.debug("Usuario guardado con id: {}", entity.getId());
        return toDomain(entity);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        log.debug("Encontrando usuario con id: {}", id);
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPassword()
        );
    }
}
