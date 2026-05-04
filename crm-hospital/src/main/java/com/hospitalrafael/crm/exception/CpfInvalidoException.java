package com.hospitalrafael.crm.exception;

public class CpfInvalidoException extends RuntimeException {
    public CpfInvalidoException(String cpf) {
        super("CPF inválido: " + cpf + ". O CPF deve conter exatamente 11 dígitos numéricos.");
    }
}
