package com.sioma.spotsapi.domain.repository;

import com.sioma.spotsapi.domain.model.Finca;

public interface FincaRepository {
    Finca save(Finca finca);
    boolean existsByNombreIgnoreCaseAndUsuarioId(String nombre, Long usuarioId);
}
