package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.Planta;
import com.sioma.spotsapi.web.dto.PlantaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlantaResponseMapper {
    PlantaResponse toResponse(Planta planta);
    List<PlantaResponse> toResponseList(List<Planta> plantas);
}
