package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.infrastructure.persistence.entities.LoteEntity;
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
        return new LoteEntity(
                domain.getNombre(),
                domain.getGeocerca(),
                domain.getFincaId(),
                domain.getTipoCultivoId()
        );
    }
}
