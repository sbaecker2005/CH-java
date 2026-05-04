package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.ai.AiLeadService;
import com.hospitalrafael.crm.dto.agendamento.AgendamentoRequest;
import com.hospitalrafael.crm.dto.agendamento.AgendamentoResponse;
import com.hospitalrafael.crm.exception.AgendamentoConflitanteException;
import com.hospitalrafael.crm.exception.AgendamentoDataPassadaException;
import com.hospitalrafael.crm.mapper.AgendamentoMapper;
import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.Usuario;
import com.hospitalrafael.crm.repository.AgendamentoRepository;
import com.hospitalrafael.crm.repository.LeadRepository;
import com.hospitalrafael.crm.repository.NotificacaoRepository;
import com.hospitalrafael.crm.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Testes Unitários")
class AgendamentoServiceTest {

    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private NotificacaoRepository notificacaoRepository;
    @Mock private LeadRepository leadRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AgendamentoMapper agendamentoMapper;
    @Mock private AiLeadService aiLeadService;

    @InjectMocks private AgendamentoService agendamentoService;

    private AgendamentoRequest requestBase;
    private Agendamento agendamentoBase;
    private Usuario operador;
    private Lead lead;
    private AgendamentoResponse responseBase;

    @BeforeEach
    void setUp() {
        operador = Usuario.builder().id(1L).nome("Operador Teste").build();
        lead     = Lead.builder().id(1L).nome("Paciente Teste").build();

        requestBase = new AgendamentoRequest();
        requestBase.setLeadId(1L);
        requestBase.setOperadorId(1L);
        requestBase.setProcedimento("Consulta Cardiologista");
        requestBase.setDataHora(LocalDateTime.now().plusDays(5));

        agendamentoBase = Agendamento.builder()
                .id(1L)
                .lead(lead)
                .operador(operador)
                .procedimento("Consulta Cardiologista")
                .dataHora(LocalDateTime.now().plusDays(5))
                .lembreteEnviado(false)
                .build();

        responseBase = new AgendamentoResponse();
        responseBase.setId(1L);
    }

    // ─── Testes de Criação ────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve criar agendamento com data futura")
    void deveCriarComSucesso() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(agendamentoRepository.existsByOperadorIdAndDataHora(any(), any())).thenReturn(false);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoBase);
        when(agendamentoMapper.toResponse(any())).thenReturn(responseBase);
        when(aiLeadService.gerarMensagemLembrete(any(), any(), any())).thenReturn("Lembrete ok");
        when(notificacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AgendamentoResponse resultado = agendamentoService.criar(requestBase);

        assertNotNull(resultado);
        verify(leadRepository).findById(1L);
        verify(usuarioRepository).findById(1L);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar AgendamentoDataPassadaException para data no passado")
    void deveLancarExceptionParaDataPassada() {
        requestBase.setDataHora(LocalDateTime.now().minusDays(1));
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));

        assertThrows(AgendamentoDataPassadaException.class,
                () -> agendamentoService.criar(requestBase));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar AgendamentoConflitanteException para horário ocupado")
    void deveLancarExceptionParaConflito() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(lead));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(operador));
        when(agendamentoRepository.existsByOperadorIdAndDataHora(
                eq(1L), eq(requestBase.getDataHora()))).thenReturn(true);

        assertThrows(AgendamentoConflitanteException.class,
                () -> agendamentoService.criar(requestBase));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reagendar para nova data futura")
    void deveReagendarComSucesso() {
        LocalDateTime novaData = LocalDateTime.now().plusDays(10);
        AgendamentoResponse reagendado = new AgendamentoResponse();

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoBase));
        when(agendamentoRepository.existsByOperadorIdAndDataHora(any(), eq(novaData))).thenReturn(false);
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(agendamentoMapper.toResponse(any())).thenReturn(reagendado);

        AgendamentoResponse resultado = agendamentoService.reagendar(1L, novaData);

        assertNotNull(resultado);
        verify(agendamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve confirmar agendamento com sucesso")
    void deveConfirmarAgendamento() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoBase));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(agendamentoMapper.toResponse(any())).thenReturn(responseBase);

        AgendamentoResponse resultado = agendamentoService.confirmar(1L);
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Deve enviar lembretes apenas para agendamentos próximos (≤ 24h)")
    void deveEnviarLembretes() {
        Agendamento proximo = Agendamento.builder()
                .dataHora(LocalDateTime.now().plusHours(12))
                .lembreteEnviado(false)
                .build();
        Agendamento distante = Agendamento.builder()
                .dataHora(LocalDateTime.now().plusDays(30))
                .lembreteEnviado(false)
                .build();

        when(agendamentoRepository.findByLembreteEnviadoFalse()).thenReturn(List.of(proximo, distante));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        int count = agendamentoService.enviarLembretes();

        assertEquals(1, count);
    }

    @Test
    @DisplayName("Deve cancelar agendamento existente")
    void deveCancelarAgendamento() {
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamentoBase));
        when(agendamentoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> agendamentoService.cancelar(1L));
    }
}
