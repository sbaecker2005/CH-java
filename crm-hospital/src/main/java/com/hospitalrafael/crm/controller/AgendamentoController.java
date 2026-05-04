package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.dto.agendamento.AgendamentoRequest;
import com.hospitalrafael.crm.dto.agendamento.AgendamentoResponse;
import com.hospitalrafael.crm.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
@Validated
@Tag(name = "Agendamentos", description = "Gestão de agendamentos com validação de conflito e lembretes por IA")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    @PostMapping
    @Operation(summary = "Criar agendamento",
               description = "Cria agendamento com validação de data futura, conflito de horário e lembrete automático por IA")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário com outro agendamento"),
        @ApiResponse(responseCode = "422", description = "Data informada está no passado")
    })
    public ResponseEntity<AgendamentoResponse> criar(@Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agendamentoService.criar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os agendamentos",
               description = "Suporta paginação: ?page=0&size=20&sort=dataHora,asc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public ResponseEntity<Page<AgendamentoResponse>> listar(
            @PageableDefault(size = 20, sort = "dataHora") Pageable pageable) {
        return ResponseEntity.ok(agendamentoService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar agendamento por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(agendamentoService.buscarPorId(id));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "Listar agendamentos de um lead")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AgendamentoResponse>> listarPorLead(@PathVariable @Positive Long leadId) {
        return ResponseEntity.ok(agendamentoService.listarPorLead(leadId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar agendamentos por status",
               description = "Status possíveis: Pendente, Confirmado, Reagendado, Cancelado, Realizado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<AgendamentoResponse>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(agendamentoService.listarPorStatus(status));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar agendamento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Agendamento confirmado"),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<AgendamentoResponse> confirmar(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(agendamentoService.confirmar(id));
    }

    @PatchMapping("/{id}/reagendar")
    @Operation(summary = "Reagendar para nova data",
               description = "Body: {\"dataHora\": \"2025-12-01T10:00:00\"}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reagendado com sucesso"),
        @ApiResponse(responseCode = "409", description = "Conflito de horário"),
        @ApiResponse(responseCode = "422", description = "Data está no passado")
    })
    public ResponseEntity<AgendamentoResponse> reagendar(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, String> body) {
        LocalDateTime novaData = LocalDateTime.parse(body.get("dataHora"));
        return ResponseEntity.ok(agendamentoService.reagendar(id, novaData));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar agendamento")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    public ResponseEntity<Void> cancelar(@PathVariable @Positive Long id) {
        agendamentoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
