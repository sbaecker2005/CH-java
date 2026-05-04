package com.hospitalrafael.crm.model.converter;

import com.hospitalrafael.crm.model.enums.StatusAgendamento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persiste StatusAgendamento como string legível no banco ("Pendente", "Confirmado", etc.)
 * em vez do nome do enum, mantendo compatibilidade com dados existentes.
 */
@Converter(autoApply = true)
public class StatusAgendamentoConverter implements AttributeConverter<StatusAgendamento, String> {

    @Override
    public String convertToDatabaseColumn(StatusAgendamento attribute) {
        return attribute == null ? null : attribute.getValor();
    }

    @Override
    public StatusAgendamento convertToEntityAttribute(String dbData) {
        return dbData == null ? null : StatusAgendamento.fromValor(dbData);
    }
}
