package com.hospitalrafael.crm.controller;

import com.hospitalrafael.crm.dto.interacao.InteracaoRequest;
import com.hospitalrafael.crm.dto.interacao.InteracaoResponse;
import com.hospitalrafael.crm.service.InteracaoService;
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

@RestController
@RequestMapping("/api/interacoes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Interações", description = "Registro de interações com detecção semântica de urgência via IA")
public class InteracaoController {

    private final InteracaoService interacaoService;

    @PostMapping
    @Operation(summary = "Registrar interação",
               description = "Registra interação e analisa o conteúdo com IA para detectar urgência automaticamente")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Interação registrada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Lead ou operador não encontrado")
    })
    public ResponseEntity<InteracaoResponse> registrar(@Valid @RequestBody InteracaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(interacaoService.registrar(request));
    }

    @GetMapping
    @Operation(summary = "Listar todas as interações",
               description = "Suporta paginação: ?page=0&size=20&sort=realizadoEm,desc")
    @ApiResponse(responseCode = "200", description = "Página retornada com sucesso")
    public ResponseEntity<Page<InteracaoResponse>> listar(
            @PageableDefault(size = 20, sort = "realizadoEm") Pageable pageable) {
        return ResponseEntity.ok(interacaoService.listar(pageable));
    }

    @GetMapping("/lead/{leadId}")
    @Operation(summary = "Listar interações de um lead")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<InteracaoResponse>> listarPorLead(@PathVariable @Positive Long leadId) {
        return ResponseEntity.ok(interacaoService.listarPorLead(leadId));
    }

    @GetMapping("/operador/{operadorId}")
    @Operation(summary = "Listar interações por operador")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<InteracaoResponse>> listarPorOperador(@PathVariable @Positive Long operadorId) {
        return ResponseEntity.ok(interacaoService.listarPorOperador(operadorId));
    }

    @GetMapping("/urgentes")
    @Operation(summary = "Listar interações com urgência detectada pela IA",
               description = "Retorna apenas as interações onde a IA identificou risco ou urgência clínica")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<InteracaoResponse>> listarUrgentes() {
        return ResponseEntity.ok(interacaoService.listarUrgentes());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover interação")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Interação não encontrada")
    })
    public ResponseEntity<Void> remover(@PathVariable @Positive Long id) {
        interacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
