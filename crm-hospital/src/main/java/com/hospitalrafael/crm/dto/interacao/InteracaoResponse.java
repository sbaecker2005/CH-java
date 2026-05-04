package com.hospitalrafael.crm.dto.interacao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospitalrafael.crm.dto.lead.LeadResumoResponse;
import com.hospitalrafael.crm.dto.usuario.UsuarioResumoResponse;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InteracaoResponse {
    private Long id;
    private LeadResumoResponse lead;
    private UsuarioResumoResponse operador;
    private String tipo;
    private String conteudo;
    private Boolean urgenciaDetectada;
    private String urgenciaNivel;
    private LocalDate realizadoEm;
}
