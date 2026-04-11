package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLotesByFincaIdUseCase {
    private final LoteRepository repository;
    private final FincaRepository fincaRepository;

    public PageResult<Lote> execute(Long fincaId, int page, int size) {
        log.debug("Buscando lotes de la finca id: {}, page: {}, size: {}", fincaId, page, size);

        fincaRepository.findById(fincaId)
                .orElseThrow(() -> new FincaNotFoundException(fincaId));

        return repository.findAllByFincaId(fincaId, page, size);
    }
}
