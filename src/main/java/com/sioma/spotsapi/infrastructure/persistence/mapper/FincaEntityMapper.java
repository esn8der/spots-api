package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.infrastructure.persistence.entity.FincaEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class FincaEntityMapper {
    public Finca toDomain(@NonNull FincaEntity entity) {
        return new Finca(
                entity.getId(),
                entity.getNombre(),
                entity.getUsuarioId()
        );
    }

    public FincaEntity toEntity(@NonNull Finca domain) {
        return new FincaEntity(
                domain.getNombre(),
                domain.getUsuarioId()
        );
    }
}
