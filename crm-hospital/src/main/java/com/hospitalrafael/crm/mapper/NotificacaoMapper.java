package com.hospitalrafael.crm.mapper;

import com.hospitalrafael.crm.dto.notificacao.NotificacaoResponse;
import com.hospitalrafael.crm.model.Notificacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LeadMapper.class, UsuarioMapper.class})
public interface NotificacaoMapper {
    NotificacaoResponse toResponse(Notificacao notificacao);
}
