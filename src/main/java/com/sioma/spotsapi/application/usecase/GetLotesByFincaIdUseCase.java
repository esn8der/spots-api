package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetLotesByFincaIdUseCase {
    private final LoteRepository repository;
    private final FincaRepository fincaRepository;

    public GetLotesByFincaIdUseCase(LoteRepository repository, FincaRepository fincaRepository) {
        this.repository = repository;
        this.fincaRepository = fincaRepository;
    }

    public List<Lote> execute(Long id) {
        if(fincaRepository.findById(id).isEmpty()) {
            throw new FincaNotFoundException(id);
        }

        return repository.findAllByFincaId(id);
    }
}
