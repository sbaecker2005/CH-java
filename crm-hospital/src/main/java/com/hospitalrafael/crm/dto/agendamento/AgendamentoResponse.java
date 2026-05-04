package com.hospitalrafael.crm.dto.agendamento;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospitalrafael.crm.dto.lead.LeadResumoResponse;
import com.hospitalrafael.crm.dto.usuario.UsuarioResumoResponse;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgendamentoResponse {
    private Long id;
    private LeadResumoResponse lead;
    private UsuarioResumoResponse operador;
    private String procedimento;
    private LocalDateTime dataHora;
    private String status;
    private Boolean lembreteEnviado;
    private LocalDate criadoEm;
}
