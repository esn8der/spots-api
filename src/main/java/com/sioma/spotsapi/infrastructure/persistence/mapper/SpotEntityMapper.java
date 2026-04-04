package com.sioma.spotsapi.infrastructure.persistence.mapper;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.infrastructure.persistence.entities.SpotEntity;
import org.springframework.stereotype.Component;

@Component
public class SpotEntityMapper {

    public Spot toDomain(SpotEntity entity) {
        return new Spot(
                entity.getId(),
                entity.getCoordenada(),
                entity.getLoteId(),
                entity.getLinea(),
                entity.getPosicion()
        );
    }

    public SpotEntity toEntity(Spot domain) {
        return new SpotEntity(
                domain.getCoordenada(),
                domain.getLoteId(),
                domain.getLinea(),
                domain.getPosicion()
        );
    }
}
