package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;
import com.sioma.spotsapi.domain.model.PaginationParams;
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

    public PageResult<Finca> execute(Long usuarioId, PaginationParams params) {
        log.debug("Buscando fincas del usuario id: {}, page: {}, size: {}", usuarioId, params.page(), params.size());

        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        return repository.findAllByUsuarioId(usuarioId, params);
    }
}
