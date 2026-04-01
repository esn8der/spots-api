package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Finca;

import java.util.List;
import java.util.Optional;

public interface FincaRepository {
    Finca save(Finca finca);
    void deleteById(Long id);
    Optional<Finca> findById(Long id);
    List<Finca> findAllByUsuarioId(Long id);
    boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
