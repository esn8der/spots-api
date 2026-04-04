package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.web.dto.SpotResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpotResponseMapper {
    SpotResponse toResponse(Spot spot);
    List<SpotResponse> toResponseList(List<Spot> spots);
}
