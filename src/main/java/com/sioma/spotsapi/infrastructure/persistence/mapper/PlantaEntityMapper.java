package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.infrastructure.persistence.entity.PlantaEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PlantaEntityMapper {

    public Planta toDomain(@NonNull PlantaEntity entity) {
        return new Planta(entity.getId(), entity.getNombre());
    }

    public PlantaEntity toEntity(@NonNull Planta domain) {
        return new PlantaEntity(domain.getNombre());
    }
}
