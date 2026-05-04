package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "USUARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_usuario")
    @SequenceGenerator(name = "sq_usuario", sequenceName = "SQ_USUARIO", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 80)
    @Column(nullable = false, length = 80)
    private String senha;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve ter exatamente 11 dígitos numéricos")
    @Column(name = "CPF", nullable = false, unique = true, length = 11)
    private String cpf;

    @Size(max = 20)
    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos numéricos")
    @Column(length = 20)
    private String telefone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "DATANASC", nullable = false)
    private LocalDate dataNasc;

    @Column(length = 20)
    @Builder.Default
    private String role = "OPERADOR";
}
