package com.hospitalrafael.crm.dto.usuario;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioResumoResponse {
    private Long id;
    private String nome;
    private String email;
}
