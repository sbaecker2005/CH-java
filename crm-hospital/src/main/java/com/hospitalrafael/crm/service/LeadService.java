package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.dashboard.DashboardResponse;
import com.hospitalrafael.crm.dto.lead.LeadRequest;
import com.hospitalrafael.crm.dto.lead.LeadResponse;
import com.hospitalrafael.crm.exception.DadosInvalidosException;
import com.hospitalrafael.crm.exception.LeadDuplicadoException;
import com.hospitalrafael.crm.exception.LeadStatusInvalidoException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.LeadMapper;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.model.enums.StatusLead;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas regras de negócio de Leads.
 *
 * Responsabilidades:
 *  - Cadastro com detecção de duplicidade
 *  - Cálculo automático de lead score e prioridade
 *  - Atualização de status com validação de transição
 *  - Listagem por prioridade e urgência
 *  - Dashboard com métricas de negócio
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LeadService {

    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final LeadMapper leadMapper;

    // ─── REGRA DE NEGÓCIO 1: Cadastro com cálculo de Lead Score ──────────────

    /**
     * Cadastra um novo Lead no sistema.
     *
     * Aplica automaticamente:
     *  - Validação de duplicidade por email
     *  - Cálculo do Lead Score baseado nos fatores (urgência, canal, plano)
     *  - Definição da prioridade numérica (1=máxima a 4=mínima)
     *  - Status inicial: NOVO
     */
    public LeadResponse cadastrar(LeadRequest request) {
        if (leadRepository.existsByEmail(request.getEmail())) {
            throw new LeadDuplicadoException(request.getEmail());
        }
        Lead lead = leadMapper.toEntity(request);
        if (request.getOperadorId() != null) {
            Usuario operador = usuarioRepository.findById(request.getOperadorId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Operador", request.getOperadorId()));
            lead.setOperador(operador);
        }
        calcularLeadScore(lead);
        log.info("Cadastrando lead: {}", request.getEmail());
        return leadMapper.toResponse(leadRepository.save(lead));
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de status com validação ─────────────

    /**
     * Atualiza o status de um Lead respeitando as transições permitidas.
     *
     * Transições válidas:
     *  NOVO → EM_ATENDIMENTO | CANCELADO
     *  EM_ATENDIMENTO → AGUARDANDO_RETORNO | CONVERTIDO | CANCELADO
     *  AGUARDANDO_RETORNO → EM_ATENDIMENTO | CANCELADO
     */
    public LeadResponse atualizarStatus(Long id, String novoStatusStr) {
        if (novoStatusStr == null || novoStatusStr.isBlank()) {
            throw new DadosInvalidosException("O status não pode ser vazio");
        }
        Lead lead = buscarEntidadePorId(id);
        StatusLead novoStatus = StatusLead.fromValor(novoStatusStr);
        validarTransicaoStatus(lead.getStatus(), novoStatus);
        lead.setStatus(novoStatus);
        return leadMapper.toResponse(leadRepository.save(lead));
    }

    // ─── REGRA DE NEGÓCIO 3: Atribuição de operador ───────────────────────────

    /**
     * Atribui (ou reatribui) um operador a um lead.
     * Atualiza também o fator de tempo sem resposta.
     */
    public LeadResponse atribuirOperador(Long leadId, Long operadorId) {
        Lead lead = buscarEntidadePorId(leadId);
        Usuario operador = usuarioRepository.findById(operadorId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador", operadorId));
        lead.setOperador(operador);
        lead.setUltimoContato(LocalDate.now());
        lead.setFatorTempoSemResposta("Menos de 24h");
        return leadMapper.toResponse(leadRepository.save(lead));
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public LeadResponse buscarPorId(Long id) {
        return leadMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<LeadResponse> listar(Pageable pageable) {
        return leadRepository.findAll(pageable).map(leadMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> listarTodos() {
        return leadRepository.findAll().stream().map(leadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> listarPorPrioridade() {
        return leadRepository.findAllByOrderByPrioridadeAsc().stream()
                .map(leadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> listarUrgentes() {
        return leadRepository.findLeadsUrgentesOrdenados().stream()
                .map(leadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<LeadResponse> listarPorStatus(String status) {
        StatusLead statusEnum = StatusLead.fromValor(status);
        return leadRepository.findByStatusOrderByPrioridadeAsc(statusEnum).stream()
                .map(leadMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        List<Lead> todos = leadRepository.findAll();

        long total = todos.size();
        long urgentes = todos.stream().filter(l -> Boolean.TRUE.equals(l.getFatorUrgencia())).count();
        long convertidos = todos.stream().filter(l -> StatusLead.CONVERTIDO.equals(l.getStatus())).count();
        double taxaConversao = total > 0 ? (convertidos * 100.0 / total) : 0;

        // Contagem por status usando groupingBy — uma única passagem na lista
        Map<StatusLead, Long> countPorStatus = todos.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getStatus() != null ? l.getStatus() : StatusLead.NOVO,
                        Collectors.counting()
                ));

        Map<String, Long> porStatus = new LinkedHashMap<>();
        for (StatusLead s : StatusLead.values()) {
            porStatus.put(s.getValor(), countPorStatus.getOrDefault(s, 0L));
        }

        long agendamentosHoje = agendamentoRepository.findByData(LocalDate.now()).size();
        long naoLidas = notificacaoRepository.countByLidaFalse();

        return DashboardResponse.builder()
                .totalLeads(total)
                .leadsNovos(countPorStatus.getOrDefault(StatusLead.NOVO, 0L))
                .leadsUrgentes(urgentes)
                .leadsConvertidos(convertidos)
                .taxaConversao(Math.round(taxaConversao * 10.0) / 10.0)
                .leadsPorStatus(porStatus)
                .agendamentosHoje(agendamentosHoje)
                .notificacoesNaoLidas(naoLidas)
                .build();
    }

    public void remover(Long id) {
        leadRepository.delete(buscarEntidadePorId(id));
    }

    // ─── Lógica de cálculo de Lead Score (privada) ────────────────────────────

    /**
     * Calcula o Lead Score e a prioridade com base nos fatores:
     *  - fator_urgencia   (+3 pontos)
     *  - canal_origem     (+2 para Indicação/WhatsApp, +1 para outros)
     *  - plano_saude      (+1 se tiver plano)
     *
     * Score final:
     *  >= 5  → "Muito Alto" / prioridade 1
     *  >= 3  → "Alto"       / prioridade 2
     *  >= 1  → "Médio"      / prioridade 3
     *  < 1   → "Baixo"      / prioridade 4
     */
    private void calcularLeadScore(Lead lead) {
        int pontos = 0;

        if (Boolean.TRUE.equals(lead.getFatorUrgencia())) {
            pontos += 3;
        }

        if (lead.getCanalOrigem() != null) {
            String canal = lead.getCanalOrigem().toLowerCase();
            if (canal.contains("indicação") || canal.contains("indicacao") || canal.contains("whatsapp")) {
                pontos += 2;
                lead.setFatorCanal("Alto");
            } else {
                pontos += 1;
                lead.setFatorCanal("Médio");
            }
        }

        if (lead.getPlanoSaude() != null && !lead.getPlanoSaude().isBlank()) {
            pontos += 1;
        }

        if (pontos >= 5) {
            lead.setLeadScore("Muito Alto");
            lead.setPrioridade(1);
        } else if (pontos >= 3) {
            lead.setLeadScore("Alto");
            lead.setPrioridade(2);
        } else if (pontos >= 1) {
            lead.setLeadScore("Médio");
            lead.setPrioridade(3);
        } else {
            lead.setLeadScore("Baixo");
            lead.setPrioridade(4);
        }
    }

    private void validarTransicaoStatus(StatusLead statusAtual, StatusLead novoStatus) {
        StatusLead atual = statusAtual != null ? statusAtual : StatusLead.NOVO;
        boolean transicaoValida = switch (atual) {
            case NOVO -> novoStatus == StatusLead.EM_ATENDIMENTO || novoStatus == StatusLead.CANCELADO;
            case EM_ATENDIMENTO -> novoStatus == StatusLead.AGUARDANDO_RETORNO
                    || novoStatus == StatusLead.CONVERTIDO
                    || novoStatus == StatusLead.CANCELADO;
            case AGUARDANDO_RETORNO -> novoStatus == StatusLead.EM_ATENDIMENTO
                    || novoStatus == StatusLead.CANCELADO;
            case CONVERTIDO, CANCELADO -> false;
        };

        if (!transicaoValida) {
            throw new LeadStatusInvalidoException(
                    atual.getValor(), "mudar para '" + novoStatus.getValor() + "'");
        }
    }

    private Lead buscarEntidadePorId(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lead", id));
    }

    // ─── Acesso interno (para outros services) ───────────────────────────────

    public Lead buscarEntidade(Long id) {
        return buscarEntidadePorId(id);
    }
}
