package com.hospitalrafael.crm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler global de exceptions — captura e formata todos os erros da API
 * em um padrão consistente de resposta JSON.
 *
 * Convenção de status HTTP utilizada:
 *  400 Bad Request       → dados de entrada inválidos (campo obrigatório, formato incorreto)
 *  401 Unauthorized      → não autenticado
 *  404 Not Found         → recurso não existe
 *  409 Conflict          → violação de unicidade (e-mail/CPF duplicado, conflito de horário)
 *  422 Unprocessable     → dados válidos sintaticamente mas que violam regra de negócio
 *  500 Internal Server   → erro inesperado do servidor (mensagem genérica, detalhes logados)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private Map<String, Object> buildError(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("erro", erro);
        body.put("mensagem", mensagem);
        return body;
    }

    // ─── 404 Not Found ────────────────────────────────────────────────────────

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage()));
    }

    // ─── 409 Conflict — unicidade e conflitos de horário ─────────────────────

    @ExceptionHandler(LeadDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleLeadDuplicado(LeadDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, "Lead duplicado", ex.getMessage()));
    }

    @ExceptionHandler(EmailDuplicadoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(EmailDuplicadoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, "E-mail duplicado", ex.getMessage()));
    }

    @ExceptionHandler(AgendamentoConflitanteException.class)
    public ResponseEntity<Map<String, Object>> handleAgendamentoConflitante(AgendamentoConflitanteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(HttpStatus.CONFLICT, "Conflito de agendamento", ex.getMessage()));
    }

    // ─── 422 Unprocessable Entity — violações de regra de negócio ────────────

    @ExceptionHandler(AgendamentoDataPassadaException.class)
    public ResponseEntity<Map<String, Object>> handleAgendamentoDataPassada(AgendamentoDataPassadaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, "Data inválida", ex.getMessage()));
    }

    @ExceptionHandler(LeadStatusInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleLeadStatusInvalido(LeadStatusInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, "Transição de status inválida", ex.getMessage()));
    }

    @ExceptionHandler(DadosInvalidosException.class)
    public ResponseEntity<Map<String, Object>> handleDadosInvalidos(DadosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(HttpStatus.UNPROCESSABLE_ENTITY, "Dados inválidos", ex.getMessage()));
    }

    // ─── 400 Bad Request — @Positive / @NotNull em @PathVariable (@Validated) ──

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        String mensagem = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "Parâmetro inválido", mensagem));
    }

    // ─── 400 Bad Request — campos obrigatórios e CPF ─────────────────────────

    @ExceptionHandler(CpfInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleCpfInvalido(CpfInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, "CPF inválido", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            campos.put(campo, error.getDefaultMessage());
        });

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("erro", "Erro de validação");
        body.put("campos", campos);
        return ResponseEntity.badRequest().body(body);
    }

    // ─── 500 Internal Server Error — sem vazamento de detalhes internos ───────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Erro inesperado no servidor", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno do servidor",
                        "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde."));
    }
}
