package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateFincaUseCase {
    private final FincaRepository fincaRepository;
    private final UsuarioRepository usuarioRepository;

    public CreateFincaUseCase(FincaRepository fincaRepository, UsuarioRepository usuarioRepository) {
        this.fincaRepository = fincaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Finca execute(String nombre, Long usuarioId) {

        if (usuarioRepository.findById(usuarioId).isEmpty()) {
            throw new UsuarioNotFoundException(usuarioId);
        }
        if (fincaRepository.existsByNombreIgnoreCaseAndUsuarioId(nombre, usuarioId)) {
            throw new FincaAlreadyExistsException();
        }

        return fincaRepository.save(new Finca(nombre, usuarioId));
    }
}
