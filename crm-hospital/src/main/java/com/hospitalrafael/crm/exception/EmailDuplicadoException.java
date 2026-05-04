package com.hospitalrafael.crm.exception;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String email) {
        super("Já existe um usuário cadastrado com o email: " + email);
    }
}
