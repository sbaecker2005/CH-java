package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "NOTIFICACAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_notificacao")
    @SequenceGenerator(name = "sq_notificacao", sequenceName = "SQ_NOTIFICACAO", allocationSize = 1)
    private Long id;

    @NotNull(message = "Lead é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID")
    private Usuario operador;

    @Size(max = 200)
    @Column(length = 200)
    private String mensagem;

    @Size(max = 40)
    @Column(name = "LEAD_NOME", length = 40)
    private String leadNome;

    /** Indica se a mensagem foi gerada por IA */
    @Column(name = "GERADO_POR_IA")
    @Builder.Default
    private Boolean geradoPorIa = false;

    @Column(name = "LIDA")
    @Builder.Default
    private Boolean lida = false;

    @Column(name = "CRIADO_EM")
    private LocalDate criadoEm;

    @PrePersist
    protected void onCreate() {
        this.criadoEm = LocalDate.now();
        if (this.geradoPorIa == null) this.geradoPorIa = false;
        if (this.lida == null) this.lida = false;
    }
}
