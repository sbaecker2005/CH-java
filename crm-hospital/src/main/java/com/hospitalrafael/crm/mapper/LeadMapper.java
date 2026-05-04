package com.hospitalrafael.crm.mapper;

import com.hospitalrafael.crm.dto.lead.LeadRequest;
import com.hospitalrafael.crm.dto.lead.LeadResponse;
import com.hospitalrafael.crm.dto.lead.LeadResumoResponse;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.enums.StatusLead;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UsuarioMapper.class})
public interface LeadMapper {

    LeadResponse toResponse(Lead lead);

    LeadResumoResponse toResumoResponse(Lead lead);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "leadScore", ignore = true)
    @Mapping(target = "prioridade", ignore = true)
    @Mapping(target = "fatorCanal", ignore = true)
    @Mapping(target = "fatorTempoSemResposta", ignore = true)
    @Mapping(target = "fatorReagendamento", ignore = true)
    @Mapping(target = "ultimoContato", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "operador", ignore = true)
    Lead toEntity(LeadRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "operador", ignore = true)
    void updateFromRequest(LeadRequest request, @MappingTarget Lead lead);

    /** MapStruct usa este método automaticamente para converter StatusLead → String no toResponse. */
    default String map(StatusLead status) {
        return status != null ? status.getValor() : null;
    }
}
