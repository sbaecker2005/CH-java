package com.hospitalrafael.crm.model;

import com.hospitalrafael.crm.model.enums.StatusAgendamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "AGENDAMENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_agendamento")
    @SequenceGenerator(name = "sq_agendamento", sequenceName = "SQ_AGENDAMENTO", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID", nullable = false)
    private Usuario operador;

    @Size(max = 40)
    @Column(length = 40)
    private String procedimento;

    @Column(name = "DATA_HORA")
    private LocalDateTime dataHora;

    @Column(length = 40)
    @Builder.Default
    private StatusAgendamento status = StatusAgendamento.PENDENTE;

    @Column(name = "LEMBRETE_ENVIADO")
    @Builder.Default
    private Boolean lembreteEnviado = false;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
        if (this.lembreteEnviado == null) this.lembreteEnviado = false;
        if (this.status == null) this.status = StatusAgendamento.PENDENTE;
    }
}
