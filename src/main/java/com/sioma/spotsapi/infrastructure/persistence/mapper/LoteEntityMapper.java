package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.infrastructure.persistence.entity.LoteEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class LoteEntityMapper {

    public Lote toDomain(@NonNull LoteEntity entity) {
        return new Lote(
                entity.getId(),
                entity.getNombre(),
                entity.getGeocerca(),
                entity.getFincaId(),
                entity.getTipoCultivoId()
        );
    }

    public LoteEntity toEntity(@NonNull Lote domain) {
        LoteEntity entity = new LoteEntity(
                domain.getNombre(),
                domain.getGeocerca(),
                domain.getFincaId(),
                domain.getTipoCultivoId()
        );
        entity.setId(domain.getId());
        return entity;
    }
}
