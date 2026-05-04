package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.ai.AiLeadService;
import com.hospitalrafael.crm.dto.dashboard.DashboardResponse;
import com.hospitalrafael.crm.model.RelatorioIa;
import com.hospitalrafael.crm.repository.RelatorioIaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Serviço de relatórios gerados por IA.
 *
 * Executa um job diário às 7h que:
 *  1. Coleta métricas do CRM
 *  2. Envia para Claude gerar análise executiva
 *  3. Salva o relatório no banco
 *  4. Faz push via WebSocket para os operadores conectados
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RelatorioIaService {

    private final RelatorioIaRepository relatorioIaRepository;
    private final LeadService leadService;
    private final AiLeadService aiLeadService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Job diário de relatório — executa às 7h todos os dias.
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void gerarRelatoriodiario() {
        log.info("Iniciando geração do relatório diário de IA...");
        try {
            RelatorioIa relatorio = gerarEsalvar();
            messagingTemplate.convertAndSend("/topic/relatorio", relatorio);
            log.info("Relatório diário gerado e enviado via WebSocket: id={}", relatorio.getId());
        } catch (Exception e) {
            log.error("Falha ao gerar relatório diário: {}", e.getMessage(), e);
        }
    }

    /**
     * Gera um relatório sob demanda (chamável via controller ou job).
     */
    public RelatorioIa gerarEsalvar() {
        DashboardResponse dashboard = leadService.getDashboard();

        int total      = (int) dashboard.getTotalLeads();
        int novos      = (int) dashboard.getLeadsNovos();
        int convertidos= (int) dashboard.getLeadsConvertidos();
        int urgentes   = (int) dashboard.getLeadsUrgentes();
        double taxa    = dashboard.getTaxaConversao();

        String resumo = aiLeadService.gerarRelatorioExecutivo(total, novos, convertidos, urgentes, taxa);

        String recomendacoes = urgentes > 0
                ? String.format("⚠️ %d leads urgentes precisam de atenção imediata. Taxa de conversão atual: %.1f%%.", urgentes, taxa)
                : String.format("✅ Nenhum lead urgente. Taxa de conversão: %.1f%%. Foco em novos leads: %d.", taxa, novos);

        RelatorioIa relatorio = RelatorioIa.builder()
                .dataReferencia(LocalDate.now())
                .geradoEm(LocalDateTime.now())
                .totalLeads(total)
                .leadsNovos(novos)
                .leadsConvertidos(convertidos)
                .leadsUrgentes(urgentes)
                .taxaConversao(taxa)
                .resumoIa(resumo)
                .recomendacoesIa(recomendacoes)
                .build();

        return relatorioIaRepository.save(relatorio);
    }

    @Transactional(readOnly = true)
    public Optional<RelatorioIa> buscarUltimo() {
        return relatorioIaRepository.findTopByOrderByGeradoEmDesc();
    }

    @Transactional(readOnly = true)
    public Optional<RelatorioIa> buscarPorData(LocalDate data) {
        return relatorioIaRepository.findByDataReferencia(data);
    }
}
