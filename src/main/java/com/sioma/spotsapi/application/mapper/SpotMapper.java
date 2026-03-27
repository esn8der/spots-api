package com.sioma.spotsapi.application.mapper;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.web.dto.SpotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotMapper {
    SpotResponse toResponse(Spot spot);
    List<SpotResponse> toResponseList(List<Spot> spots);
}
