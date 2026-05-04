package com.hospitalrafael.crm.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusLead {

    NOVO("Novo"),
    EM_ATENDIMENTO("Em Atendimento"),
    AGUARDANDO_RETORNO("Aguardando Retorno"),
    CONVERTIDO("Convertido"),
    CANCELADO("Cancelado");

    private final String valor;

    StatusLead(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return valor;
    }

    @JsonCreator
    public static StatusLead fromValor(String valor) {
        if (valor == null) return NOVO;
        for (StatusLead s : values()) {
            if (s.valor.equalsIgnoreCase(valor) || s.name().equalsIgnoreCase(valor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de lead inválido: " + valor);
    }
}
