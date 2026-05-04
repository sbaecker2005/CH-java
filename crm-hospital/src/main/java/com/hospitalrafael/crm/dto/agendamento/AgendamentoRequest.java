package com.hospitalrafael.crm.dto.agendamento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgendamentoRequest {

    @NotNull(message = "Lead é obrigatório")
    @Schema(example = "1")
    private Long leadId;

    @NotNull(message = "Operador é obrigatório")
    @Schema(example = "1")
    private Long operadorId;

    @Size(max = 40)
    @Schema(example = "Consulta Cardiologista")
    private String procedimento;

    @NotNull(message = "Data e hora são obrigatórias")
    @Schema(example = "2025-12-15T10:00:00")
    private LocalDateTime dataHora;
}
