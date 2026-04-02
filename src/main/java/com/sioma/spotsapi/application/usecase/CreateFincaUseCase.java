package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateFincaUseCase {
    private final FincaRepository fincaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Finca execute(String nombre, Long usuarioId) {
        log.debug("Creando finca con nombre: {}, usuarioId: {}", nombre, usuarioId);
        if (usuarioRepository.findById(usuarioId).isEmpty()) {
            throw new UsuarioNotFoundException(usuarioId);
        }
        if (fincaRepository.existsByNombreIgnoreCaseAndUsuarioId(nombre, usuarioId)) {
            throw new FincaAlreadyExistsException(nombre, usuarioId);
        }

        log.info("Finca creada exitosamente con nombre: {}", nombre);
        return fincaRepository.save(new Finca(nombre, usuarioId));
    }
}
