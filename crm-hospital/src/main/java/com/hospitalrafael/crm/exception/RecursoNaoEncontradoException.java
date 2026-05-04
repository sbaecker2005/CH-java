package com.hospitalrafael.crm.exception;

// Arquivo de exports públicos das exceptions
// Cada classe pública para uso nos Services e Controllers

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String recurso, Long id) {
        super(recurso + " com ID " + id + " não foi encontrado.");
    }
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
