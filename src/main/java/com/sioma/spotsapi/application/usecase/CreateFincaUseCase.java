package com.sioma.spotsapi.application.usecase;

import com.sioma.spotsapi.domain.exception.FincaAlreadyExistsException;
import com.sioma.spotsapi.domain.exception.UsuarioNotExistsException;
import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.repository.FincaRepository;
import com.sioma.spotsapi.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateFincaUseCase {
    private final FincaRepository fincaRepository;
    private final UsuarioRepository usuarioRepository;

    public CreateFincaUseCase(FincaRepository fincaRepository, UsuarioRepository usuarioRepository) {
        this.fincaRepository = fincaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Finca execute(String nombre, Long idUsuario) {

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new UsuarioNotExistsException(idUsuario);
        }
        if (fincaRepository.existsByNombreIgnoreCaseAndIdUsuario(nombre, idUsuario)) {
            throw new FincaAlreadyExistsException();
        }

        return fincaRepository.save(new Finca(nombre, idUsuario));
    }
}
