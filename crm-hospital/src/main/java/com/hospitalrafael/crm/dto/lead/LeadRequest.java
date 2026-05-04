package com.hospitalrafael.crm.dto.lead;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class LeadRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 40)
    @Schema(example = "Maria da Silva")
    private String nome;

    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos numéricos")
    @Size(max = 20)
    @Schema(example = "11987654321")
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email
    @Size(max = 40)
    @Schema(example = "maria@email.com")
    private String email;

    @Size(max = 40)
    @Schema(example = "WhatsApp")
    private String canalOrigem;

    @Size(max = 40)
    @Schema(example = "Unimed")
    private String planoSaude;

    @Size(max = 40)
    @Schema(example = "Consulta Cardiologista")
    private String procedimentoInteresse;

    @Schema(example = "false")
    private Boolean fatorUrgencia;

    @Schema(example = "1")
    private Long operadorId;
}
