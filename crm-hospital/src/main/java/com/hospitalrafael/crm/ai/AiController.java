package com.hospitalrafael.crm.ai;

import com.hospitalrafael.crm.dto.ai.LeadAnalysisResponse;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Inteligência Artificial", description = "Análise de leads e detecção de urgência via Inteligência Artificial")
public class AiController {

    private final AiLeadService aiLeadService;
    private final LeadService leadService;

    @PostMapping("/leads/{id}/analisar")
    @Operation(
        summary = "Analisar lead com IA",
        description = "Usa IA para analisar o perfil completo do lead e "
                    + "retornar recomendação estratégica, mensagem sugerida e probabilidade de conversão."
    )
    public ResponseEntity<LeadAnalysisResponse> analisarLead(@PathVariable Long id) {
        Lead lead = leadService.buscarEntidade(id);
        LeadAnalysisResponse analise = aiLeadService.analisarLead(lead);
        return ResponseEntity.ok(analise);
    }

    @PostMapping("/urgencia")
    @Operation(
        summary = "Detectar urgência em texto",
        description = "Analisa semanticamente um texto de interação e determina o nível de urgência (CRITICO/ALTO/NORMAL)"
    )
    public ResponseEntity<?> detectarUrgencia(@RequestBody String conteudo) {
        return ResponseEntity.ok(aiLeadService.detectarUrgencia(conteudo));
    }
}
