package com.hospitalrafael.crm.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Resposta analitica do dashboard executivo.
 * Inclui KPIs operacionais e quebras analiticas para visualizacao em graficos.
 */
@Data
@Builder
public class DashboardResponse {

    // ─── KPIs principais ────────────────────────────────────────────
    private long totalLeads;
    private long leadsNovos;
    private long leadsUrgentes;
    private long leadsConvertidos;
    private double taxaConversao;
    private double taxaCancelamento;
    private double taxaUrgencia;

    // ─── Distribuicoes para graficos ────────────────────────────────
    private Map<String, Long> leadsPorStatus;
    private Map<String, Long> leadsPorCanal;
    private Map<String, Long> leadsPorScore;
    private Map<String, Long> leadsPorMes;          // ultimos 6 meses (yyyy-MM → contagem)

    // ─── Rankings (top N) ───────────────────────────────────────────
    private List<RankingItem> topProcedimentos;     // top 5 por demanda
    private List<RankingItem> topCanaisConversao;   // top 5 por taxa de conversao
    private List<RankingItem> topOperadores;        // top 5 por leads atribuidos

    // ─── Operacional ────────────────────────────────────────────────
    private long agendamentosHoje;
    private long agendamentosSemana;
    private long notificacoesNaoLidas;

    @Data
    @Builder
    public static class RankingItem {
        private String label;
        private double valor;       // contagem ou percentual conforme contexto
        private String descricao;   // texto opcional ("32% conversao")
    }
}
