package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.notificacao.NotificacaoResponse;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.NotificacaoMapper;
import com.hospitalrafael.crm.model.Notificacao;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final NotificacaoMapper notificacaoMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── REGRA DE NEGÓCIO 1: Criação com push em tempo real ──────────────────

    /**
     * Cria uma nova notificação e a transmite via WebSocket
     * para todos os operadores conectados em tempo real.
     */
    public Notificacao criar(Notificacao notificacao) {
        if (notificacao.getMensagem() != null && notificacao.getMensagem().length() > 200) {
            notificacao.setMensagem(notificacao.getMensagem().substring(0, 200));
        }
        if (notificacao.getLead() != null) {
            notificacao.setLeadNome(notificacao.getLead().getNome());
        }

        Notificacao salva = notificacaoRepository.save(notificacao);

        // Push WebSocket em tempo real
        try {
            NotificacaoResponse response = notificacaoMapper.toResponse(salva);
            messagingTemplate.convertAndSend("/topic/notificacoes", response);
            log.debug("Notificação enviada via WebSocket: {}", salva.getId());
        } catch (Exception e) {
            log.warn("Falha ao enviar WebSocket: {}", e.getMessage());
        }

        return salva;
    }

    // ─── REGRA DE NEGÓCIO 2: Marcar como lida ────────────────────────────────

    public NotificacaoResponse marcarComoLida(Long id) {
        Notificacao notificacao = buscarEntidadePorId(id);
        notificacao.setLida(true);
        return notificacaoMapper.toResponse(notificacaoRepository.save(notificacao));
    }

    // ─── REGRA DE NEGÓCIO 3: Atualização de mensagem ─────────────────────────

    public Notificacao atualizarMensagem(Long id, String novaMensagem) {
        Notificacao notificacao = buscarEntidadePorId(id);
        notificacao.setMensagem(novaMensagem.length() > 200
                ? novaMensagem.substring(0, 200)
                : novaMensagem);
        return notificacaoRepository.save(notificacao);
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarTodas() {
        return notificacaoRepository.findAll().stream()
                .map(notificacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarNaoLidas() {
        return notificacaoRepository.findByLidaFalse().stream()
                .map(notificacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacaoResponse> listarPorLead(Long leadId) {
        return notificacaoRepository.findByLeadId(leadId).stream()
                .map(notificacaoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas() {
        return notificacaoRepository.countByLidaFalse();
    }

    public void remover(Long id) {
        notificacaoRepository.delete(buscarEntidadePorId(id));
    }

    private Notificacao buscarEntidadePorId(Long id) {
        return notificacaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", id));
    }
}
