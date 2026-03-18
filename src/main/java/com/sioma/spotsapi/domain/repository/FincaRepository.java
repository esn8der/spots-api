package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Finca;

import java.util.List;

public interface FincaRepository {
    Finca save(Finca finca);
    boolean existsById(Long id);
    List<Finca> findAllByUsuarioId(Long id);
    boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
