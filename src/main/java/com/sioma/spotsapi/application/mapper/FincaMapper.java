package com.sioma.spotsapi.application.mapper;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.web.dto.FincaResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FincaMapper {
    FincaResponse toResponse(Finca finca);
    List<FincaResponse> toResponseList(List<Finca> fincas);
}
