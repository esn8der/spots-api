package com.sioma.spotsapi.application.usecase;


import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetUsuarioByIdUseCase {
    private final UsuarioRepository repository;

    public Usuario execute(Long id) {
        log.debug("Buscando usuario con id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));
    }
}
