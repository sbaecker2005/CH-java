package com.hospitalrafael.crm.dto.notificacao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospitalrafael.crm.dto.lead.LeadResumoResponse;
import com.hospitalrafael.crm.dto.usuario.UsuarioResumoResponse;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificacaoResponse {
    private Long id;
    private LeadResumoResponse lead;
    private UsuarioResumoResponse operador;
    private String mensagem;
    private String leadNome;
    private Boolean geradoPorIa;
    private Boolean lida;
    private LocalDate criadoEm;
}
