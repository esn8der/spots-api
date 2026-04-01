package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteUsuarioByIdUseCase {
    private final UsuarioRepository repository;

    @Transactional
    public void execute(Long id) {
        if (id == null || id <= 0) {
            log.error("ID de usuario inválido para eliminación: {}", id);
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }

        log.debug("Intentando eliminar usuario con id: {}", id);

        if (repository.findById(id).isEmpty()) {
            log.warn("Usuario con id: {} no encontrada para eliminar", id);
            throw new UsuarioNotFoundException(id);
        }

        repository.deleteById(id);
        log.info("Usuario con id: {} eliminada exitosamente", id);
    }
}
