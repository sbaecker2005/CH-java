package com.hospitalrafael.crm.mapper;

import com.hospitalrafael.crm.dto.agendamento.AgendamentoResponse;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.enums.StatusAgendamento;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LeadMapper.class, UsuarioMapper.class})
public interface AgendamentoMapper {

    AgendamentoResponse toResponse(Agendamento agendamento);

    /** MapStruct usa este método automaticamente para converter StatusAgendamento → String no toResponse. */
    default String map(StatusAgendamento status) {
        return status != null ? status.getValor() : null;
    }
}
