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
        log.debug("Intentando eliminar usuario con id: {}", id);

        repository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        repository.deleteById(id);
        log.info("Usuario con id: {} eliminada exitosamente", id);
    }
}
