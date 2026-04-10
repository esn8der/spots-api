package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.LoteAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.LoteNotFoundException;
import com.sioma.spotsapi.domain.model.Lote;
import com.sioma.spotsapi.domain.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateLoteUseCase {

    private final LoteRepository repository;

    @Transactional
    public Lote execute(Long id, String nombre) {
        log.debug("Actualizando nombre de lote, id: {}, nuevo nombre: {}", id, nombre);

        Lote lote = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Lote no encontrado para actualización, id: {}", id);
                    return new LoteNotFoundException(id);
                });

        if (repository.existsByNombreIgnoreCaseAndFincaId(nombre, lote.getFincaId())) {
            throw new LoteAlreadyExistsException(nombre, lote.getFincaId());
        }

        log.info("Nombre de lote actualizado exitosamente, id: {}", id);
        return repository.save(lote.renombrar(nombre));
    }
}