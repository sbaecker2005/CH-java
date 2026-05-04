package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.ai.AiLeadService;
import com.hospitalrafael.crm.dto.ai.UrgencyAnalysisResponse;
import com.hospitalrafael.crm.dto.interacao.InteracaoRequest;
import com.hospitalrafael.crm.dto.interacao.InteracaoResponse;
import com.hospitalrafael.crm.exception.RecursoNaoEncontradoException;
import com.hospitalrafael.crm.mapper.InteracaoMapper;
import com.hospitalrafael.crm.model.Interacao;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.model.enums.UrgenciaNivel;
import com.hospitalrafael.crm.repository.InteracaoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InteracaoService - Testes Unitários")
class InteracaoServiceTest {

    @Mock private InteracaoRepository interacaoRepository;
    @Mock private LeadRepository leadRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private InteracaoMapper interacaoMapper;
    @Mock private AiLeadService aiLeadService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    @InjectMocks private InteracaoService interacaoService;

    private InteracaoRequest requestBase;
    private Lead lead;
    private Usuario operador;
    private Interacao interacaoBase;
    private InteracaoResponse responseBase;

    @BeforeEach
    void setUp() {
        lead     = Lead.builder().id(1L).nome("Paciente Teste").fatorUrgencia(false).build();
        operador = Usuario.builder().id(1L).nome("Operador Teste").build();

        requestBase = new InteracaoRequest();
        requestBase.setLeadId(1L);
        requestBase.setOperadorId(1L);
        requestBase.setTipo("Ligação");
        requestBase.setConteudo("Paciente relatou dor leve.");

        interacaoBase = Interacao.builder()
                .id(1L)
                .lead(lead)
                .operador(operador)
                .tipo("Ligação")
                .conteudo("Paciente relatou dor leve.")
                .urgenciaDetectada(false)
                .urgenciaNivel(UrgenciaNivel.NORMAL)
                .build();

        responseBase = new InteracaoResponse();
        responseBase.setId(1L);
        responseBase.setUrgenciaNivel("NORMAL");
    }

    // ─── Registro com urgência NORMAL ────────────────────────────────────────

    @Test
    @DisplayName("Deve registrar interação com urgência NORMAL sem acionar WebSocket")
    void deveRegistrarComUrgenciaNormal() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(aiLeadService.detectarUrgencia(any()))
                .thenReturn(new UrgencyAnalysisResponse(false, "NORMAL", "Sem urgência"));
        when(interacaoRepository.save(any())).thenReturn(interacaoBase);
        when(leadRepository.save(any())).thenReturn(lead);
        when(interacaoMapper.toResponse(any())).thenReturn(responseBase);

        InteracaoResponse resultado = interacaoService.registrar(requestBase);

        assertNotNull(resultado);
        assertEquals("NORMAL", resultado.getUrgenciaNivel());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
        verify(interacaoRepository).save(any());
    }

    // ─── Registro com urgência CRÍTICA → push WebSocket + lead atualizado ────

    @Test
    @DisplayName("Deve acionar WebSocket e atualizar lead quando urgência for CRITICA")
    void deveAcionarWebSocketParaUrgenciaCritica() {
        Interacao interacaoCritica = Interacao.builder()
                .id(2L)
                .lead(lead)
                .operador(operador)
                .urgenciaDetectada(true)
                .urgenciaNivel(UrgenciaNivel.CRITICO)
                .build();

        InteracaoResponse responseCritico = new InteracaoResponse();
        responseCritico.setUrgenciaNivel("CRITICO");

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(aiLeadService.detectarUrgencia(any()))
                .thenReturn(new UrgencyAnalysisResponse(true, "CRITICO", "Dor no peito — risco cardíaco"));
        when(interacaoRepository.save(any())).thenReturn(interacaoCritica);
        when(leadRepository.save(any())).thenReturn(lead);
        when(interacaoMapper.toResponse(any())).thenReturn(responseCritico);

        interacaoService.registrar(requestBase);

        // WebSocket deve ser acionado exatamente 1 vez para /topic/leads/urgentes
        verify(messagingTemplate).convertAndSend(eq("/topic/leads/urgentes"), any(Object.class));
        // Lead deve ter fatorUrgencia atualizado para true
        assertTrue(lead.getFatorUrgencia());
    }

    // ─── Lead não encontrado ──────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException para lead inexistente")
    void deveLancarExceptionParaLeadInexistente() {
        when(leadRepository.findById(99L)).thenReturn(Optional.empty());

        requestBase.setLeadId(99L);
        assertThrows(RecursoNaoEncontradoException.class,
                () -> interacaoService.registrar(requestBase));
        verify(interacaoRepository, never()).save(any());
    }

    // ─── Operador não encontrado ──────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException para operador inexistente")
    void deveLancarExceptionParaOperadorInexistente() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        requestBase.setOperadorId(99L);
        assertThrows(RecursoNaoEncontradoException.class,
                () -> interacaoService.registrar(requestBase));
        verify(interacaoRepository, never()).save(any());
    }

    // ─── Urgência detectada propaga fatorUrgencia ao lead ────────────────────

    @Test
    @DisplayName("Deve propagar fatorUrgencia=true ao lead quando IA detectar urgência")
    void devePropagar_fatorUrgencia_quandoIaDetectarUrgencia() {
        Interacao interacaoAlta = Interacao.builder()
                .id(3L)
                .lead(lead)
                .operador(operador)
                .urgenciaDetectada(true)
                .urgenciaNivel(UrgenciaNivel.ALTO)
                .build();

        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(aiLeadService.detectarUrgencia(any()))
                .thenReturn(new UrgencyAnalysisResponse(true, "ALTO", "Sintoma preocupante"));
        when(interacaoRepository.save(any())).thenReturn(interacaoAlta);
        when(leadRepository.save(any())).thenReturn(lead);
        when(interacaoMapper.toResponse(any())).thenReturn(responseBase);

        interacaoService.registrar(requestBase);

        assertTrue(lead.getFatorUrgencia());
        // leadRepository.save chamado pelo menos 1x (urgência) + 1x (último contato)
        verify(leadRepository, atLeast(1)).save(lead);
    }

    // ─── Listar urgentes ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar apenas interações com urgência detectada")
    void deveListarUrgentes() {
        when(interacaoRepository.findByUrgenciaDetectadaTrue()).thenReturn(List.of(interacaoBase));
        when(interacaoMapper.toResponse(any())).thenReturn(responseBase);

        var urgentes = interacaoService.listarUrgentes();

        assertEquals(1, urgentes.size());
        verify(interacaoRepository).findByUrgenciaDetectadaTrue();
    }

    // ─── Listar por lead ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve listar interações filtradas pelo ID do lead")
    void deveListarPorLead() {
        when(interacaoRepository.findByLeadId(1L)).thenReturn(List.of(interacaoBase));
        when(interacaoMapper.toResponse(any())).thenReturn(responseBase);

        var resultado = interacaoService.listarPorLead(1L);

        assertEquals(1, resultado.size());
        verify(interacaoRepository).findByLeadId(1L);
    }

    // ─── Remoção de interação ─────────────────────────────────────────────────

    @Test
    @DisplayName("Deve remover interação existente sem lançar exceção")
    void deveRemoverInteracao() {
        when(interacaoRepository.findById(1L)).thenReturn(Optional.of(interacaoBase));
        doNothing().when(interacaoRepository).delete(interacaoBase);

        assertDoesNotThrow(() -> interacaoService.remover(1L));
        verify(interacaoRepository).delete(interacaoBase);
    }
}
