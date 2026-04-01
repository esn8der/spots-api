package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.UsuarioNotFoundException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFincasByUsuarioIdUseCase {
    private final FincaRepository repository;
    private final UsuarioRepository usuarioRepository;

    public GetFincasByUsuarioIdUseCase(FincaRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Finca> execute(Long id) {
        if (usuarioRepository.findById(id).isEmpty()) {
            throw new UsuarioNotFoundException(id);
        }

        return repository.findAllByUsuarioId(id);
    }
}
