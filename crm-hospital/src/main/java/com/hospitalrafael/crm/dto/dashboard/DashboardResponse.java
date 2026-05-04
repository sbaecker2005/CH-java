package com.hospitalrafael.crm.dto.dashboard;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {
    private long totalLeads;
    private long leadsNovos;
    private long leadsUrgentes;
    private long leadsConvertidos;
    private double taxaConversao;
    private Map<String, Long> leadsPorStatus;
    private long agendamentosHoje;
    private long notificacoesNaoLidas;
}
