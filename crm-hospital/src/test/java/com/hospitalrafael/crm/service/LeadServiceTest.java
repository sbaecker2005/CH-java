package com.hospitalrafael.crm.service;

import com.hospitalrafael.crm.dto.lead.LeadRequest;
import com.hospitalrafael.crm.dto.lead.LeadResponse;
import com.hospitalrafael.crm.exception.LeadDuplicadoException;
import com.hospitalrafael.crm.exception.LeadStatusInvalidoException;
import com.hospitalrafael.crm.mapper.LeadMapper;
import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.enums.StatusLead;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeadService - Testes Unitários")
class LeadServiceTest {

    @Mock private LeadRepository leadRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AgendamentoRepository agendamentoRepository;
    @Mock private NotificacaoRepository notificacaoRepository;
    @Mock private LeadMapper leadMapper;

    @InjectMocks private LeadService leadService;

    private LeadRequest requestBase;
    private Lead leadBase;
    private LeadResponse responseBase;

    @BeforeEach
    void setUp() {
        requestBase = new LeadRequest();
        requestBase.setNome("Ana Pereira");
        requestBase.setEmail("ana@email.com");
        requestBase.setTelefone("11977770001");
        requestBase.setCanalOrigem("Instagram");
        requestBase.setPlanoSaude("Unimed");
        requestBase.setFatorUrgencia(true);

        leadBase = Lead.builder()
                .id(1L)
                .nome("Ana Pereira")
                .email("ana@email.com")
                .status(StatusLead.NOVO)
                .fatorUrgencia(true)
                .canalOrigem("Instagram")
                .planoSaude("Unimed")
                .build();

        responseBase = new LeadResponse();
        responseBase.setId(1L);
        responseBase.setNome("Ana Pereira");
        responseBase.setEmail("ana@email.com");
        responseBase.setLeadScore("Muito Alto");
        responseBase.setPrioridade(1);
        responseBase.setStatus("Novo");
    }

    // ─── Testes de Cadastro ───────────────────────────────────────────────────

    @Test
    @DisplayName("Deve cadastrar lead com sucesso e calcular score")
    void deveCadastrarLeadComSucesso() {
        when(leadRepository.existsByEmail(requestBase.getEmail())).thenReturn(false);
        when(leadMapper.toEntity(any())).thenReturn(leadBase);
        when(leadRepository.save(any(Lead.class))).thenReturn(leadBase);
        when(leadMapper.toResponse(any())).thenReturn(responseBase);

        LeadResponse resultado = leadService.cadastrar(requestBase);

        assertNotNull(resultado);
        assertNotNull(resultado.getNome());
        verify(leadRepository, times(1)).save(any(Lead.class));
    }

    @Test
    @DisplayName("Deve lançar LeadDuplicadoException para email já existente")
    void deveLancarExceptionParaEmailDuplicado() {
        when(leadRepository.existsByEmail(requestBase.getEmail())).thenReturn(true);

        assertThrows(LeadDuplicadoException.class, () -> leadService.cadastrar(requestBase));
        verify(leadRepository, never()).save(any());
    }

    // ─── Testes de Lead Score ─────────────────────────────────────────────────

    @Test
    @DisplayName("Lead urgente com indicação deve ter score Muito Alto")
    void leadUrgenteComIndicacaoDeveSerMuitoAlto() {
        requestBase.setFatorUrgencia(true);
        requestBase.setCanalOrigem("Indicação");
        requestBase.setPlanoSaude("Unimed");
        leadBase.setCanalOrigem("Indicação");

        when(leadRepository.existsByEmail(any())).thenReturn(false);
        when(leadMapper.toEntity(any())).thenReturn(leadBase);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any())).thenAnswer(inv -> {
            Lead l = inv.getArgument(0);
            LeadResponse resp = new LeadResponse();
            resp.setLeadScore(l.getLeadScore());
            resp.setPrioridade(l.getPrioridade());
            return resp;
        });

        LeadResponse resultado = leadService.cadastrar(requestBase);

        assertEquals("Muito Alto", resultado.getLeadScore());
        assertEquals(1, resultado.getPrioridade());
    }

    @Test
    @DisplayName("Lead sem fatores deve ter score Baixo")
    void leadSemFatoresDeveSerBaixo() {
        requestBase.setFatorUrgencia(false);
        requestBase.setCanalOrigem(null);
        requestBase.setPlanoSaude(null);
        leadBase.setFatorUrgencia(false);
        leadBase.setCanalOrigem(null);
        leadBase.setPlanoSaude(null);

        when(leadRepository.existsByEmail(any())).thenReturn(false);
        when(leadMapper.toEntity(any())).thenReturn(leadBase);
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any())).thenAnswer(inv -> {
            Lead l = inv.getArgument(0);
            LeadResponse resp = new LeadResponse();
            resp.setLeadScore(l.getLeadScore());
            resp.setPrioridade(l.getPrioridade());
            return resp;
        });

        LeadResponse resultado = leadService.cadastrar(requestBase);

        assertEquals("Baixo", resultado.getLeadScore());
        assertEquals(4, resultado.getPrioridade());
    }

    // ─── Testes de Atualização de Status ─────────────────────────────────────

    @Test
    @DisplayName("Deve permitir transição de Novo para Em Atendimento")
    void devePermitirTransicaoNovoParaEmAtendimento() {
        leadBase.setStatus(StatusLead.NOVO);
        LeadResponse emAtendimento = new LeadResponse();
        emAtendimento.setStatus("Em Atendimento");

        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadBase));
        when(leadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(leadMapper.toResponse(any())).thenReturn(emAtendimento);

        LeadResponse resultado = leadService.atualizarStatus(1L, "Em Atendimento");

        assertEquals("Em Atendimento", resultado.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exception para transição inválida de status")
    void deveLancarExceptionParaTransicaoInvalida() {
        leadBase.setStatus(StatusLead.CONVERTIDO);
        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadBase));

        assertThrows(LeadStatusInvalidoException.class,
                () -> leadService.atualizarStatus(1L, "Novo"));
    }

    @Test
    @DisplayName("Deve lançar exception para status vazio")
    void deveLancarExceptionParaStatusVazio() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadBase));

        assertThrows(Exception.class, () -> leadService.atualizarStatus(1L, ""));
    }

    @Test
    @DisplayName("Deve retornar leads urgentes")
    void deveListarLeadsUrgentes() {
        when(leadRepository.findLeadsUrgentesOrdenados()).thenReturn(List.of(leadBase));
        when(leadMapper.toResponse(any())).thenReturn(responseBase);

        var urgentes = leadService.listarUrgentes();

        assertFalse(urgentes.isEmpty());
        verify(leadRepository).findLeadsUrgentesOrdenados();
    }
}
