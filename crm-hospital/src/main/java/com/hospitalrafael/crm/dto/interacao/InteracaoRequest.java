package com.hospitalrafael.crm.dto.interacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InteracaoRequest {

    @NotNull(message = "Lead é obrigatório")
    @Schema(example = "1")
    private Long leadId;

    @NotNull(message = "Operador é obrigatório")
    @Schema(example = "1")
    private Long operadorId;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 40)
    @Schema(example = "Ligação")
    private String tipo;

    @Size(max = 2000)
    @Schema(example = "Paciente relatou dor no peito com irradiação para o braço esquerdo.")
    private String conteudo;
}
