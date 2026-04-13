
package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
import com.sioma.spotsapi.domain.model.Spot;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import com.sioma.spotsapi.domain.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSpotsByLoteIdUseCase {
    private final SpotRepository spotRepository;
    private final LoteRepository loteRepository;

    public PageResult<Spot> execute(Long loteId, @NonNull PaginationParams params) {
        log.debug("Buscando spots del lote id: {}, page: {}, size: {}", loteId, params.page(), params.size());

        loteRepository.findById(loteId)
                .orElseThrow(() -> new LoteNotFoundException(loteId));

        return spotRepository.findByLoteId(loteId, params);
    }
}