package com.hospitalrafael.crm.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UrgenciaNivel {

    CRITICO("CRITICO"),
    ALTO("ALTO"),
    MEDIO("MEDIO"),
    BAIXO("BAIXO"),
    NORMAL("NORMAL");

    private final String valor;

    UrgenciaNivel(String valor) {
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
    public static UrgenciaNivel fromValor(String valor) {
        if (valor == null) return NORMAL;
        for (UrgenciaNivel u : values()) {
            if (u.valor.equalsIgnoreCase(valor) || u.name().equalsIgnoreCase(valor)) {
                return u;
            }
        }
        return NORMAL;
    }
}
