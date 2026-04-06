package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetFincasByUsuarioIdUseCase {
    private final FincaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public List<Finca> execute(Long id) {
        usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNotFoundException(id));

        log.debug("Buscando fincas del usuario con id: {}", id);
        return repository.findAllByUsuarioId(id);
    }
}
