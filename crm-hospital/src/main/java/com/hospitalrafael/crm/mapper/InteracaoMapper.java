package com.hospitalrafael.crm.mapper;

import com.hospitalrafael.crm.dto.interacao.InteracaoResponse;
import com.hospitalrafael.crm.model.Interacao;
import com.hospitalrafael.crm.model.enums.UrgenciaNivel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LeadMapper.class, UsuarioMapper.class})
public interface InteracaoMapper {

    InteracaoResponse toResponse(Interacao interacao);

    /** MapStruct usa este método automaticamente para converter UrgenciaNivel → String no toResponse. */
    default String map(UrgenciaNivel nivel) {
        return nivel != null ? nivel.getValor() : null;
    }
}
