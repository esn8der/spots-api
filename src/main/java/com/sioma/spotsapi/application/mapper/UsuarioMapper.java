package com.sioma.spotsapi.application.mapper;

import com.sioma.spotsapi.domain.model.Usuario;
import com.sioma.spotsapi.web.dto.UsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);
    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
