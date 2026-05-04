package com.hospitalrafael.crm.exception;

public class AgendamentoDataPassadaException extends RuntimeException {
    public AgendamentoDataPassadaException() {
        super("Não é possível criar um agendamento com data no passado.");
    }
}
