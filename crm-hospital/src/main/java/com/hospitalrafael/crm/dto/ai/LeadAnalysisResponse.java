package com.hospitalrafael.crm.dto.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadAnalysisResponse {

    /** Ação recomendada para o operador */
    private String recomendacao;

    /** CRITICA | ALTA | MEDIA | BAIXA */
    private String prioridade;

    /** Rascunho de mensagem para enviar ao lead */
    private String mensagemSugerida;

    /** Justificativa da análise */
    private String justificativa;

    /** Prazo sugerido para a ação */
    private String prazo;

    /** Probabilidade estimada de conversão (0-100) */
    private Integer probabilidadeConversao;

    /** Indica se a análise foi feita pela IA real ou fallback */
    private boolean analisadoPorIa;
}
