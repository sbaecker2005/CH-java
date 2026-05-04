package com.hospitalrafael.crm.mapper;

import com.hospitalrafael.crm.dto.usuario.UsuarioRequest;
import com.hospitalrafael.crm.dto.usuario.UsuarioResponse;
import com.hospitalrafael.crm.dto.usuario.UsuarioResumoResponse;
import com.hospitalrafael.crm.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    UsuarioResumoResponse toResumoResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    Usuario toEntity(UsuarioRequest request);
}
