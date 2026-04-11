package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Finca;
import com.sioma.spotsapi.domain.model.PageResult;

import java.util.Optional;

public interface FincaRepository {
    Finca save(Finca finca);
    void deleteById(Long id);
    Optional<Finca> findById(Long id);
    PageResult<Finca> findAllByUsuarioId(Long usuarioId, int page, int size);
    boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
