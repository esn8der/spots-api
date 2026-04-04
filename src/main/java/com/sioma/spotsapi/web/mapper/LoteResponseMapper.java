package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.web.dto.LoteResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoteResponseMapper {
    LoteResponse toResponse(Lote lote);
    List<LoteResponse> toResponseList(List<Lote> lotes);
}
