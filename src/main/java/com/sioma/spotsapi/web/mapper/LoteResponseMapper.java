package com.sioma.spotsapi.web.mapper;

import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.web.dto.LoteResponse;
import com.sioma.spotsapi.web.dto.PageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LoteResponseMapper {
    LoteResponse toResponse(Lote lote);
    List<LoteResponse> toResponseList(List<Lote> lotes);

    default PageResponse<LoteResponse> toPageResponse(PageResult<Lote> pageResult) {
        return new PageResponse<>(
                toResponseList(pageResult.content()),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages()
        );
    }
}
