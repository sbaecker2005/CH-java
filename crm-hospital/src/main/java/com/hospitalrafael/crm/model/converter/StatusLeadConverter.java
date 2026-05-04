package com.hospitalrafael.crm.model.converter;

import com.hospitalrafael.crm.model.enums.StatusLead;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Persiste StatusLead como string legível no banco ("Novo", "Em Atendimento", etc.)
 * em vez do nome do enum, tornando o banco legível sem dependência de código Java.
 */
@Converter(autoApply = true)
public class StatusLeadConverter implements AttributeConverter<StatusLead, String> {

    @Override
    public String convertToDatabaseColumn(StatusLead attribute) {
        return attribute == null ? null : attribute.getValor();
    }

    @Override
    public StatusLead convertToEntityAttribute(String dbData) {
        return dbData == null ? null : StatusLead.fromValor(dbData);
    }
}
