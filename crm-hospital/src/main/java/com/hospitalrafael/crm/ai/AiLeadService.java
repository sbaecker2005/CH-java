package com.hospitalrafael.crm.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospitalrafael.crm.dto.ai.LeadAnalysisResponse;
import com.hospitalrafael.crm.dto.ai.UrgencyAnalysisResponse;
import com.hospitalrafael.crm.model.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Serviço de IA do CRM — utiliza Claude (Anthropic) via Spring AI.
 *
 * Features:
 *  1. Análise completa de leads: recomendação + mensagem sugerida + probabilidade de conversão
 *  2. Detecção semântica de urgência em interações (substitui keyword matching)
 *  3. Geração de mensagens personalizadas para notificações
 *  4. Relatório executivo diário com insights do CRM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiLeadService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT_ANALISE = """
            Você é um assistente especializado em CRM hospitalar do Hospital São Rafael.
            Sua função é analisar perfis de pacientes-leads e fornecer recomendações estratégicas
            aos operadores de atendimento.

            Contexto do sistema de Lead Score:
            - Urgência = +3 pontos
            - Canal Indicação/WhatsApp = +2 pontos, outros = +1 ponto
            - Plano de saúde = +1 ponto
            - Score Muito Alto (≥5): prioridade máxima, contato em até 2h
            - Score Alto (≥3): contato em até 24h
            - Score Médio (≥1): contato em até 48h
            - Score Baixo: contato em até 72h

            Transições de status válidas:
            Novo → Em Atendimento | Cancelado
            Em Atendimento → Aguardando Retorno | Convertido | Cancelado
            Aguardando Retorno → Em Atendimento | Cancelado

            SEMPRE responda em JSON válido, em português brasileiro, sem markdown.
            Formato obrigatório:
            {
              "recomendacao": "ação específica para o operador",
              "prioridade": "CRITICA|ALTA|MEDIA|BAIXA",
              "mensagemSugerida": "rascunho de mensagem para o paciente (WhatsApp/SMS)",
              "justificativa": "explicação da análise",
              "prazo": "até Xh | até X dias",
              "probabilidadeConversao": 0-100
            }
            """;

    private static final String SYSTEM_PROMPT_URGENCIA = """
            Você é um classificador de urgência médica para o Hospital São Rafael.
            Analise o texto de uma interação com um paciente e determine o nível de urgência.

            Critérios:
            - CRITICO: risco de vida, emergência imediata, dor intensa, sintomas graves
            - ALTO: situação preocupante que requer atenção rápida (dentro de horas)
            - NORMAL: consulta de rotina, agendamento comum, dúvidas gerais

            SEMPRE responda em JSON válido, em português brasileiro, sem markdown.
            Formato obrigatório:
            {
              "urgente": true|false,
              "nivel": "CRITICO|ALTO|NORMAL",
              "justificativa": "explicação em uma frase"
            }
            """;

    /**
     * Analisa um lead com IA e retorna recomendações estratégicas.
     * Com fallback inteligente caso a IA não esteja disponível.
     */
    public LeadAnalysisResponse analisarLead(Lead lead) {
        String prompt = buildLeadPrompt(lead);
        try {
            String response = chatClient.prompt()
                    .system(SYSTEM_PROMPT_ANALISE)
                    .user(prompt)
                    .call()
                    .content();

            LeadAnalysisResponse analysis = objectMapper.readValue(
                    cleanJson(response), LeadAnalysisResponse.class
            );
            analysis.setAnalisadoPorIa(true);
            log.info("IA analisou lead {} ({}): prioridade={}", lead.getId(), lead.getNome(), analysis.getPrioridade());
            return analysis;

        } catch (Exception e) {
            log.warn("Falha na análise de IA para lead {}: {} — usando fallback", lead.getId(), e.getMessage());
            return fallbackAnalise(lead);
        }
    }

    /**
     * Detecta urgência semanticamente usando IA.
     * Substitui o sistema antigo de keywords hardcoded.
     */
    public UrgencyAnalysisResponse detectarUrgencia(String conteudo) {
        if (conteudo == null || conteudo.isBlank()) {
            return new UrgencyAnalysisResponse(false, "NORMAL", "Sem conteúdo para analisar");
        }
        try {
            String response = chatClient.prompt()
                    .system(SYSTEM_PROMPT_URGENCIA)
                    .user("Analise a urgência desta mensagem de paciente: \"" + conteudo + "\"")
                    .call()
                    .content();

            UrgencyAnalysisResponse result = objectMapper.readValue(
                    cleanJson(response), UrgencyAnalysisResponse.class
            );
            log.debug("IA detectou urgência: nivel={}, urgente={}", result.getNivel(), result.isUrgente());
            return result;

        } catch (Exception e) {
            log.warn("Falha na detecção de urgência por IA: {} — usando fallback", e.getMessage());
            return fallbackUrgencia(conteudo);
        }
    }

    /**
     * Gera uma mensagem de lembrete personalizada para o paciente.
     */
    public String gerarMensagemLembrete(String nomePaciente, String procedimento, String dataHora) {
        try {
            String prompt = String.format(
                    "Crie uma mensagem de lembrete de consulta hospitalar (máximo 150 caracteres, "
                    + "tom profissional e cordial) para:\n"
                    + "Paciente: %s\nProcedimento: %s\nData/Hora: %s\n"
                    + "Assine como: Hospital São Rafael",
                    nomePaciente, procedimento, dataHora
            );
            String mensagem = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
            return mensagem != null ? mensagem.trim() : gerarLembreteSimples(nomePaciente, procedimento, dataHora);
        } catch (Exception e) {
            return gerarLembreteSimples(nomePaciente, procedimento, dataHora);
        }
    }

    /**
     * Gera um relatório executivo diário com base nos dados do CRM.
     */
    public String gerarRelatorioExecutivo(int totalLeads, int leadsNovos, int convertidos,
                                           int urgentes, double taxaConversao) {
        try {
            String prompt = String.format("""
                    Você é um analista sênior de CRM hospitalar.
                    Com base nos dados abaixo do Hospital São Rafael, redija um relatório executivo
                    conciso (3-4 parágrafos) com análise e recomendações estratégicas para hoje.

                    Dados do dia:
                    - Total de leads ativos: %d
                    - Leads novos hoje: %d
                    - Leads convertidos: %d
                    - Leads urgentes: %d
                    - Taxa de conversão: %.1f%%

                    Inclua: análise de desempenho, pontos de atenção e 3 ações prioritárias.
                    Responda em português brasileiro.
                    """,
                    totalLeads, leadsNovos, convertidos, urgentes, taxaConversao
            );
            return chatClient.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.warn("Falha na geração do relatório executivo: {}", e.getMessage());
            return String.format(
                    "Relatório do dia: %d leads ativos, %d novos, %d convertidos (taxa: %.1f%%). "
                    + "%d leads urgentes requerem atenção imediata.",
                    totalLeads, leadsNovos, convertidos, taxaConversao, urgentes
            );
        }
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private String buildLeadPrompt(Lead lead) {
        return String.format("""
                Analise o seguinte lead do Hospital São Rafael:

                Nome: %s
                Email: %s
                Canal de origem: %s
                Status atual: %s
                Lead Score: %s (Prioridade: %d)
                Urgência: %s
                Procedimento de interesse: %s
                Plano de saúde: %s
                Operador atribuído: %s
                Último contato: %s
                Criado em: %s
                """,
                lead.getNome(),
                lead.getEmail(),
                lead.getCanalOrigem() != null ? lead.getCanalOrigem() : "Não informado",
                lead.getStatus() != null ? lead.getStatus() : "Novo",
                lead.getLeadScore() != null ? lead.getLeadScore() : "Não calculado",
                lead.getPrioridade() != null ? lead.getPrioridade() : 4,
                Boolean.TRUE.equals(lead.getFatorUrgencia()) ? "SIM — URGENTE" : "Não",
                lead.getProcedimentoInteresse() != null ? lead.getProcedimentoInteresse() : "Não informado",
                lead.getPlanoSaude() != null ? lead.getPlanoSaude() : "Sem plano",
                lead.getOperador() != null ? lead.getOperador().getNome() : "Não atribuído",
                lead.getUltimoContato() != null ? lead.getUltimoContato().toString() : "Nunca",
                lead.getCriadoEm() != null ? lead.getCriadoEm().toString() : "Desconhecido"
        );
    }

    private String cleanJson(String response) {
        if (response == null) return "{}";
        // Remove markdown code blocks se existirem
        return response.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();
    }

    private LeadAnalysisResponse fallbackAnalise(Lead lead) {
        String prioridade = "MEDIA";
        String prazo = "48h";
        if (Boolean.TRUE.equals(lead.getFatorUrgencia())) {
            prioridade = "CRITICA";
            prazo = "2h";
        } else if ("Muito Alto".equals(lead.getLeadScore()) || "Alto".equals(lead.getLeadScore())) {
            prioridade = "ALTA";
            prazo = "24h";
        }
        return LeadAnalysisResponse.builder()
                .recomendacao("Entrar em contato com o lead via WhatsApp ou telefone")
                .prioridade(prioridade)
                .mensagemSugerida("Olá " + lead.getNome() + ", somos do Hospital São Rafael. "
                        + "Gostaríamos de agendar seu atendimento. Podemos conversar?")
                .justificativa("Análise baseada no lead score e dados cadastrais (IA indisponível)")
                .prazo("até " + prazo)
                .probabilidadeConversao(50)
                .analisadoPorIa(false)
                .build();
    }

    private UrgencyAnalysisResponse fallbackUrgencia(String conteudo) {
        String lower = conteudo.toLowerCase();
        boolean urgente = lower.contains("urgente") || lower.contains("dor") ||
                          lower.contains("emergência") || lower.contains("grave") ||
                          lower.contains("imediato") || lower.contains("socorro");
        return new UrgencyAnalysisResponse(
                urgente,
                urgente ? "ALTO" : "NORMAL",
                "Detecção por palavras-chave (IA indisponível)"
        );
    }

    private String gerarLembreteSimples(String nome, String procedimento, String dataHora) {
        return String.format("Lembrete: %s, seu %s está agendado para %s. Hospital São Rafael.",
                nome, procedimento != null ? procedimento : "atendimento", dataHora);
    }
}
