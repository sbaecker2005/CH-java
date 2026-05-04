package com.hospitalrafael.crm.dto.lead;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hospitalrafael.crm.dto.usuario.UsuarioResumoResponse;
import lombok.Data;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeadResponse {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String canalOrigem;
    private String status;
    private String leadScore;
    private Integer prioridade;
    private Boolean fatorUrgencia;
    private String fatorCanal;
    private String fatorTempoSemResposta;
    private String procedimentoInteresse;
    private String planoSaude;
    private LocalDate ultimoContato;
    private LocalDate criadoEm;
    private UsuarioResumoResponse operador;
}
