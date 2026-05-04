package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.ai.AiLeadService;
import com.hospitalrafael.crm.dto.agendamento.AgendamentoRequest;
import com.hospitalrafael.crm.dto.agendamento.AgendamentoResponse;
import com.hospitalrafael.crm.exception.AgendamentoConflitanteException;
import com.hospitalrafael.crm.exception.AgendamentoDataPassadaException;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.AgendamentoMapper;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Notificacao;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.model.enums.StatusAgendamento;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Agendamentos.
 *
 * Responsabilidades:
 *  - Criação com validação de conflito de horário e data
 *  - Reagendamento com detecção de conflito
 *  - Confirmação de agendamento
 *  - Lembretes automáticos com mensagens geradas por IA
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final NotificacaoRepository notificacaoRepository;
    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final AiLeadService aiLeadService;

    // ─── REGRA DE NEGÓCIO 1: Criação com validação de conflito e data ────────

    /**
     * Cria um novo agendamento a partir do request.
     *
     * Regras aplicadas:
     *  - Data não pode ser no passado
     *  - Operador não pode ter outro agendamento no mesmo horário exato
     *  - Gera notificação de lembrete com mensagem personalizada por IA
     */
    public AgendamentoResponse criar(AgendamentoRequest request) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lead", request.getLeadId()));
        Usuario operador = usuarioRepository.findById(request.getOperadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador", request.getOperadorId()));

        Agendamento agendamento = Agendamento.builder()
                .lead(lead)
                .operador(operador)
                .procedimento(request.getProcedimento())
                .dataHora(request.getDataHora())
                .build();

        validarDataFutura(agendamento.getDataHora());
        validarConflito(agendamento);

        Agendamento salvo = agendamentoRepository.save(agendamento);
        gerarNotificacaoLembreteComIa(salvo);

        return agendamentoMapper.toResponse(salvo);
    }

    // ─── REGRA DE NEGÓCIO 2: Reagendamento ───────────────────────────────────

    /**
     * Reagenda um agendamento existente para nova data.
     *
     * Regras aplicadas:
     *  - Nova data não pode ser no passado
     *  - Verifica conflito na nova data/operador
     *  - Atualiza status para REAGENDADO e fator_reagendamento do Lead
     */
    public AgendamentoResponse reagendar(Long id, LocalDateTime novaData) {
        Agendamento agendamento = buscarEntidadePorId(id);
        validarDataFutura(novaData);

        Agendamento temp = Agendamento.builder()
                .operador(agendamento.getOperador())
                .dataHora(novaData)
                .build();
        validarConflito(temp);

        agendamento.setDataHora(novaData);
        agendamento.setStatus(StatusAgendamento.REAGENDADO);
        agendamento.setLembreteEnviado(false);

        Lead lead = agendamento.getLead();
        if (lead != null) {
            lead.setFatorReagendamento(novaData.toLocalDate());
        }

        return agendamentoMapper.toResponse(agendamentoRepository.save(agendamento));
    }

    // ─── REGRA DE NEGÓCIO 3: Confirmação ─────────────────────────────────────

    public AgendamentoResponse confirmar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);
        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        return agendamentoMapper.toResponse(agendamentoRepository.save(agendamento));
    }

    /**
     * Job agendado: envia lembretes para agendamentos do dia seguinte.
     * Executa diariamente às 8h.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void enviarLembretesAgendados() {
        int count = enviarLembretes();
        log.info("Job de lembretes executado: {} lembretes enviados", count);
    }

    public int enviarLembretes() {
        List<Agendamento> pendentes = agendamentoRepository.findByLembreteEnviadoFalse();
        LocalDateTime limite = LocalDateTime.now().plusDays(1);

        int count = 0;
        for (Agendamento ag : pendentes) {
            if (ag.getDataHora() != null && !ag.getDataHora().isAfter(limite)) {
                ag.setLembreteEnviado(true);
                agendamentoRepository.save(ag);
                count++;
                log.debug("Lembrete marcado para agendamento {}", ag.getId());
            }
        }
        return count;
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public AgendamentoResponse buscarPorId(Long id) {
        return agendamentoMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<AgendamentoResponse> listar(Pageable pageable) {
        return agendamentoRepository.findAll(pageable).map(agendamentoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarTodos() {
        return agendamentoRepository.findAll().stream()
                .map(agendamentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorLead(Long leadId) {
        return agendamentoRepository.findByLeadId(leadId).stream()
                .map(agendamentoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorStatus(String status) {
        StatusAgendamento statusEnum = StatusAgendamento.fromValor(status);
        return agendamentoRepository.findByStatus(statusEnum).stream()
                .map(agendamentoMapper::toResponse)
                .toList();
    }

    public void cancelar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private void validarDataFutura(LocalDateTime data) {
        if (data == null || !data.isAfter(LocalDateTime.now().minusMinutes(1))) {
            throw new AgendamentoDataPassadaException();
        }
    }

    private void validarConflito(Agendamento agendamento) {
        if (agendamento.getOperador() != null && agendamento.getDataHora() != null) {
            boolean conflito = agendamentoRepository.existsByOperadorIdAndDataHora(
                    agendamento.getOperador().getId(),
                    agendamento.getDataHora()
            );
            if (conflito) {
                throw new AgendamentoConflitanteException(
                        agendamento.getDataHora().toString(),
                        agendamento.getOperador().getNome()
                );
            }
        }
    }

    /** Gera notificação de lembrete com mensagem personalizada via IA. */
    private void gerarNotificacaoLembreteComIa(Agendamento agendamento) {
        if (agendamento.getLead() == null) return;

        String dataFormatada = agendamento.getDataHora() != null
                ? agendamento.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"))
                : "data a confirmar";

        String mensagem = aiLeadService.gerarMensagemLembrete(
                agendamento.getLead().getNome(),
                agendamento.getProcedimento(),
                dataFormatada
        );

        Notificacao notificacao = Notificacao.builder()
                .lead(agendamento.getLead())
                .operador(agendamento.getOperador())
                .leadNome(agendamento.getLead().getNome())
                .mensagem(mensagem.length() > 200 ? mensagem.substring(0, 200) : mensagem)
                .geradoPorIa(true)
                .build();

        notificacaoRepository.save(notificacao);
    }

    private Agendamento buscarEntidadePorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento", id));
    }
}
