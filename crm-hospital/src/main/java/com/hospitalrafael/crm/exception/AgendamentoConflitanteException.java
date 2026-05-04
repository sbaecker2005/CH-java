package com.hospitalrafael.crm.exception;

public class AgendamentoConflitanteException extends RuntimeException {
    public AgendamentoConflitanteException(String dataHora, String operador) {
        super("Já existe um agendamento para o operador '" + operador + "' em: " + dataHora);
    }
}
