package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioEntityMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getPassword()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        return new UsuarioEntity(
                domain.getNombre(),
                domain.getEmail(),
                domain.getPassword()
        );
    }
}
