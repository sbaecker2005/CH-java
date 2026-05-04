package com.hospitalrafael.crm.exception;

public class LeadStatusInvalidoException extends RuntimeException {
    public LeadStatusInvalidoException(String statusAtual, String acaoTentada) {
        super("Lead com status '" + statusAtual + "' não pode realizar a ação: " + acaoTentada);
    }
}
