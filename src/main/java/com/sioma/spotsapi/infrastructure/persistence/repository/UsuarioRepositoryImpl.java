package com.sioma.spotsapi.infrastructure.persistence.repository;

import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import com.sioma.spotsapi.infrastructure.persistence.entities.UsuarioEntity;
import com.sioma.spotsapi.infrastructure.persistence.mapper.UsuarioEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {
    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioEntityMapper mapper;

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        log.debug("Encontrando usuario con id: {}", id);
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return jpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
