package com.hospitalrafael.crm.exception;

public class DadosInvalidosException extends RuntimeException {
    public DadosInvalidosException(String mensagem) {
        super("Dados inválidos: " + mensagem);
    }
}
