package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.web.dto.PageResponse;
import com.sioma.spotsapi.web.dto.SpotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotResponseMapper {
    List<SpotResponse> toResponseList(List<Spot> spots);

    default SpotResponse toResponse(Spot spot) {
        var point = spot.getCoordenada();
        List<Double> coordinates = List.of(point.getX(), point.getY());

        return new SpotResponse(
                spot.getId(),
                spot.getLoteId(),
                spot.getLinea(),
                spot.getPosicion(),
                coordinates
                );
    }

    default PageResponse<SpotResponse> toPageResponse(PageResult<Spot> pageResult) {
        return new PageResponse<>(
                toResponseList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
    }
}
