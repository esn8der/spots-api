package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.web.dto.FincaResponse;
import com.sioma.spotsapi.web.dto.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface FincaResponseMapper {
    FincaResponse toResponse(Finca finca);
    List<FincaResponse> toResponseList(List<Finca> fincas);

    default PageResponse<FincaResponse> toPageResponse(PageResult<Finca> pageResult) {
        return new PageResponse<>(
                toResponseList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
    }
}
