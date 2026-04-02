package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetLotesByFincaIdUseCase {
    private final LoteRepository repository;
    private final FincaRepository fincaRepository;

    public List<Lote> execute(Long id) {
        if(fincaRepository.findById(id).isEmpty()) {
            throw new FincaNotFoundException(id);
        }

        log.debug("Buscando lotes de la finca con id: {}", id);
        return repository.findAllByFincaId(id);
    }
}
