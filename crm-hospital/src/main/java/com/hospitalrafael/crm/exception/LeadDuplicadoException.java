package com.hospitalrafael.crm.exception;

public class LeadDuplicadoException extends RuntimeException {
    public LeadDuplicadoException(String email) {
        super("Já existe um Lead cadastrado com o email: " + email);
    }
}
