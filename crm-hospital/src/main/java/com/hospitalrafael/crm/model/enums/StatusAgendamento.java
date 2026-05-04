package com.hospitalrafael.crm.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusAgendamento {

    PENDENTE("Pendente"),
    CONFIRMADO("Confirmado"),
    REAGENDADO("Reagendado"),
    CANCELADO("Cancelado"),
    REALIZADO("Realizado");

    private final String valor;

    StatusAgendamento(String valor) {
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
    public static StatusAgendamento fromValor(String valor) {
        if (valor == null) return PENDENTE;
        for (StatusAgendamento s : values()) {
            if (s.valor.equalsIgnoreCase(valor) || s.name().equalsIgnoreCase(valor)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Status de agendamento inválido: " + valor);
    }
}
