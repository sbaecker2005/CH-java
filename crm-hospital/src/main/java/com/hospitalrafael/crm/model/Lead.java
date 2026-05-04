package com.hospitalrafael.crm.model;

import com.hospitalrafael.crm.model.enums.StatusLead;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "LEAD")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_lead")
    @SequenceGenerator(name = "sq_lead", sequenceName = "SQ_LEAD", allocationSize = 1)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 40)
    @Column(nullable = false, length = 40)
    private String nome;

    @Size(max = 20)
    @Pattern(regexp = "\\d{10,15}", message = "Telefone deve conter entre 10 e 15 dígitos numéricos")
    @Column(length = 20)
    private String telefone;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 40)
    @Column(nullable = false, unique = true, length = 40)
    private String email;

    @Size(max = 40)
    @Column(name = "CANAL_ORIGEM", length = 40)
    private String canalOrigem;

    @Column(length = 40)
    @Builder.Default
    private StatusLead status = StatusLead.NOVO;

    @Size(max = 40)
    @Column(name = "LEAD_SCORE", length = 40)
    private String leadScore;

    @Column
    private Integer prioridade;

    @Column(name = "FATOR_URGENCIA")
    @Builder.Default
    private Boolean fatorUrgencia = false;

    @Size(max = 30)
    @Column(name = "FATOR_CANAL", length = 30)
    private String fatorCanal;

    @Size(max = 40)
    @Column(name = "FATOR_TEMPO_SEM_RESPOSTA", length = 40)
    private String fatorTempoSemResposta;

    @Column(name = "FATOR_REAGENDAMENTO")
    private LocalDate fatorReagendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID")
    private Usuario operador;

    @Size(max = 40)
    @Column(name = "PROCEDIMENTO_INTERESSE", length = 40)
    private String procedimentoInteresse;

    @Size(max = 40)
    @Column(name = "PLANO_SAUDE", length = 40)
    private String planoSaude;

    @Column(name = "ULTIMO_CONTATO")
    private LocalDate ultimoContato;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
        if (this.status == null) this.status = StatusLead.NOVO;
        if (this.fatorUrgencia == null) this.fatorUrgencia = false;
    }
}
