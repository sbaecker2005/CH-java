package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.dto.notificacao.NotificacaoResponse;
import com.hospitalrafael.crm.service.NotificacaoService;
import com.hospitalrafael.crm.service.RelatorioIaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Notificações", description = "Notificações em tempo real via WebSocket e relatórios executivos gerados por IA")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final RelatorioIaService relatorioIaService;

    @GetMapping
    @Operation(summary = "Listar todas as notificações")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<NotificacaoResponse>> listar() {
        return ResponseEntity.ok(notificacaoService.listarTodas());
    }

    @GetMapping("/nao-lidas")
    @Operation(summary = "Listar notificações não lidas")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<NotificacaoResponse>> listarNaoLidas() {
        return ResponseEntity.ok(notificacaoService.listarNaoLidas());
    }

    @GetMapping("/nao-lidas/count")
    @Operation(summary = "Contar notificações não lidas",
               description = "Útil para exibir badge de contador em interfaces")
    @ApiResponse(responseCode = "200", description = "Contagem retornada — ex: {\"total\": 3}")
    public ResponseEntity<Map<String, Long>> contarNaoLidas() {
        return ResponseEntity.ok(Map.of("total", notificacaoService.contarNaoLidas()));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "Listar notificações de um lead")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<NotificacaoResponse>> listarPorLead(@PathVariable @Positive Long leadId) {
        return ResponseEntity.ok(notificacaoService.listarPorLead(leadId));
    }

    @PatchMapping("/{id}/lida")
    @Operation(summary = "Marcar notificação como lida")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificação marcada como lida"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover notificação")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Notificação removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> remover(@PathVariable @Positive Long id) {
        notificacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relatorio/gerar")
    @Operation(summary = "Gerar relatório executivo via IA",
               description = "Dispara análise completa do CRM pela IA e persiste o resultado no banco")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    public ResponseEntity<?> gerarRelatorio() {
        return ResponseEntity.ok(relatorioIaService.gerarEsalvar());
    }

    @GetMapping("/relatorio/ultimo")
    @Operation(summary = "Buscar último relatório gerado pela IA")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório encontrado"),
        @ApiResponse(responseCode = "204", description = "Nenhum relatório gerado ainda")
    })
    public ResponseEntity<?> ultimoRelatorio() {
        return relatorioIaService.buscarUltimo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
