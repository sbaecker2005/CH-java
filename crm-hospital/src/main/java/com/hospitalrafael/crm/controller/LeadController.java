package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.dto.dashboard.DashboardResponse;
import com.hospitalrafael.crm.dto.lead.LeadRequest;
import com.hospitalrafael.crm.dto.lead.LeadResponse;
import com.hospitalrafael.crm.service.LeadService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Validated
@Tag(name = "Leads", description = "Gestão de leads/pacientes com Lead Score automático e priorização por IA")
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    @Operation(summary = "Cadastrar lead",
               description = "Cadastra novo lead com cálculo automático de Lead Score e prioridade (1=máxima a 4=mínima)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Lead cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "E-mail já cadastrado no sistema")
    })
    public ResponseEntity<LeadResponse> cadastrar(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.cadastrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todos os leads",
               description = "Suporta paginação: ?page=0&size=20&sort=prioridade,asc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public ResponseEntity<Page<LeadResponse>> listar(
            @PageableDefault(size = 20, sort = "prioridade") Pageable pageable) {
        return ResponseEntity.ok(leadService.listar(pageable));
    }

    @GetMapping("/prioridade")
    @Operation(summary = "Listar leads ordenados por prioridade",
               description = "Retorna leads ordenados do mais prioritário (1) ao menos prioritário (4)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<LeadResponse>> listarPorPrioridade() {
        return ResponseEntity.ok(leadService.listarPorPrioridade());
    }

    @GetMapping("/urgentes")
    @Operation(summary = "Listar leads urgentes",
               description = "Retorna apenas leads com fator de urgência ativado, ordenados por prioridade")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<LeadResponse>> listarUrgentes() {
        return ResponseEntity.ok(leadService.listarUrgentes());
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar leads por status",
               description = "Status válidos: Novo, Em Atendimento, Aguardando Retorno, Convertido, Cancelado")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<LeadResponse>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(leadService.listarPorStatus(status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar lead por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lead encontrado"),
        @ApiResponse(responseCode = "404", description = "Lead não encontrado")
    })
    public ResponseEntity<LeadResponse> buscarPorId(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(leadService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do lead",
               description = "Transições válidas: Novo→Em Atendimento | Em Atendimento→Aguardando Retorno/Convertido/Cancelado | Aguardando Retorno→Em Atendimento/Cancelado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lead não encontrado"),
        @ApiResponse(responseCode = "422", description = "Transição de status não permitida")
    })
    public ResponseEntity<LeadResponse> atualizarStatus(
            @PathVariable @Positive Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(leadService.atualizarStatus(id, body.get("status")));
    }

    @PatchMapping("/{id}/operador/{operadorId}")
    @Operation(summary = "Atribuir operador ao lead",
               description = "Vincula um operador responsável ao lead e atualiza o tempo de resposta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operador atribuído com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lead ou operador não encontrado")
    })
    public ResponseEntity<LeadResponse> atribuirOperador(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Long operadorId) {
        return ResponseEntity.ok(leadService.atribuirOperador(id, operadorId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover lead", description = "Remove o lead permanentemente. Requer perfil ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Lead removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Lead não encontrado")
    })
    public ResponseEntity<Void> remover(@PathVariable @Positive Long id) {
        leadService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard executivo",
               description = "Retorna KPIs e métricas: total de leads, urgentes, convertidos, taxa de conversão e agendamentos do dia")
    @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(leadService.getDashboard());
    }
}
