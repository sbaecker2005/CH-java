package com.hospitalrafael.crm.dto.usuario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UsuarioRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 40)
    @Schema(example = "João Operador")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email
    @Size(max = 40)
    @Schema(example = "joao@hospital.com")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 40)
    @Schema(example = "senha123")
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter exatamente 11 dígitos numéricos")
    @Schema(example = "12345678901")
    private String cpf;

    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos numéricos")
    @Size(max = 20)
    @Schema(example = "11912345678")
    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Schema(example = "1990-05-20")
    private LocalDate dataNasc;

    @Schema(example = "OPERADOR")
    private String role;
}
