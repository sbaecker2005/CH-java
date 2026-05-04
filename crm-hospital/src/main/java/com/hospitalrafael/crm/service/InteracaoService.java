package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.ai.AiLeadService;
import com.hospitalrafael.crm.dto.ai.UrgencyAnalysisResponse;
import com.hospitalrafael.crm.dto.interacao.InteracaoRequest;
import com.hospitalrafael.crm.dto.interacao.InteracaoResponse;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.InteracaoMapper;
import com.hospitalrafael.crm.model.Interacao;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.enums.UrgenciaNivel;
import com.hospitalrafael.crm.repository.InteracaoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Interações.
 *
 * Responsabilidades:
 *  - Registro com detecção de urgência SEMÂNTICA via IA (Claude)
 *  - Atualização de último contato do lead
 *  - Push WebSocket quando urgência crítica é detectada
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InteracaoService {

    private final InteracaoRepository interacaoRepository;
    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;
    private final InteracaoMapper interacaoMapper;
    private final AiLeadService aiLeadService;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── REGRA DE NEGÓCIO 1: Registro com detecção de urgência por IA ────────

    /**
     * Registra uma nova interação a partir do request.
     *
     * Regras aplicadas:
     *  - Tipo de interação é obrigatório
     *  - Detecção semântica de urgência via Claude
     *  - Atualiza a data de último contato do lead
     *  - Se urgência CRITICA detectada, atualiza lead e notifica via WebSocket
     */
    public InteracaoResponse registrar(InteracaoRequest request) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Lead", request.getLeadId()));
        var operador = usuarioRepository.findById(request.getOperadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador", request.getOperadorId()));

        Interacao interacao = new Interacao();
        interacao.setLead(lead);
        interacao.setOperador(operador);
        interacao.setTipo(request.getTipo());
        interacao.setConteudo(request.getConteudo());

        detectarUrgenciaComIa(interacao);
        Interacao salva = interacaoRepository.save(interacao);
        atualizarUltimoContatoLead(interacao);

        // Push WebSocket se urgência crítica detectada
        if (UrgenciaNivel.CRITICO.equals(salva.getUrgenciaNivel()) && salva.getLead() != null) {
            try {
                messagingTemplate.convertAndSend("/topic/leads/urgentes",
                        interacaoMapper.toResponse(salva));
                log.info("Urgência CRÍTICA detectada — lead {} notificado via WebSocket",
                        salva.getLead().getId());
            } catch (Exception e) {
                log.warn("Falha ao notificar urgência via WebSocket: {}", e.getMessage());
            }
        }

        return interacaoMapper.toResponse(salva);
    }

    // ─── REGRA DE NEGÓCIO 2: Atualização de interação ────────────────────────

    public InteracaoResponse atualizar(Long id, Interacao dados) {
        Interacao existente = buscarEntidadePorId(id);
        existente.setConteudo(dados.getConteudo());
        detectarUrgenciaComIa(existente);
        return interacaoMapper.toResponse(interacaoRepository.save(existente));
    }

    // ─── REGRA DE NEGÓCIO 3: Remoção ─────────────────────────────────────────

    public void remover(Long id) {
        interacaoRepository.delete(buscarEntidadePorId(id));
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public InteracaoResponse buscarPorId(Long id) {
        return interacaoMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<InteracaoResponse> listar(Pageable pageable) {
        return interacaoRepository.findAll(pageable).map(interacaoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<InteracaoResponse> listarTodos() {
        return interacaoRepository.findAll().stream()
                .map(interacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteracaoResponse> listarPorLead(Long leadId) {
        return interacaoRepository.findByLeadId(leadId).stream()
                .map(interacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteracaoResponse> listarPorOperador(Long operadorId) {
        return interacaoRepository.findByOperadorId(operadorId).stream()
                .map(interacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InteracaoResponse> listarUrgentes() {
        return interacaoRepository.findByUrgenciaDetectadaTrue().stream()
                .map(interacaoMapper::toResponse)
                .toList();
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    /**
     * Usa IA semântica (Claude) para detectar urgência,
     * com fallback automático para keywords em caso de falha.
     */
    private void detectarUrgenciaComIa(Interacao interacao) {
        UrgencyAnalysisResponse analise = aiLeadService.detectarUrgencia(interacao.getConteudo());

        interacao.setUrgenciaDetectada(analise.isUrgente());
        interacao.setUrgenciaNivel(UrgenciaNivel.fromValor(analise.getNivel()));

        // Propaga urgência crítica/alta para o lead
        if (analise.isUrgente() && interacao.getLead() != null) {
            Lead lead = interacao.getLead();
            lead.setFatorUrgencia(true);
            leadRepository.save(lead);
            log.info("Urgência propagada para lead {} — nível: {}", lead.getId(), analise.getNivel());
        }
    }

    private void atualizarUltimoContatoLead(Interacao interacao) {
        if (interacao.getLead() != null) {
            Lead lead = interacao.getLead();
            lead.setUltimoContato(LocalDate.now());
            lead.setFatorTempoSemResposta("Menos de 24h");
            leadRepository.save(lead);
        }
    }

    private Interacao buscarEntidadePorId(Long id) {
        return interacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Interação", id));
    }
}
