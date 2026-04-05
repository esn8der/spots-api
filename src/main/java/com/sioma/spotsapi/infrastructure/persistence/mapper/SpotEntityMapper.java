package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.model.SpotPosition;
import com.sioma.spotsapi.infrastructure.persistence.entity.SpotEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

@Component
public class SpotEntityMapper {

    public Spot toDomain(@NonNull SpotEntity entity) {
        return new Spot(
                entity.getId(),
                entity.getCoordenada(),
                entity.getLoteId(),
                new SpotPosition(entity.getLinea(), entity.getPosicion())
        );
    }

    public SpotEntity toEntity(@NonNull Spot domain) {
        return new SpotEntity(
                domain.getCoordenada(),
                domain.getLoteId(),
                domain.getSpotPosicion().linea(),
                domain.getSpotPosicion().posicion()
        );
    }
}
