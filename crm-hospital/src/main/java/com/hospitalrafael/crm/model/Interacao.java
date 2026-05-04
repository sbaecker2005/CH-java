package com.hospitalrafael.crm.model;

import com.hospitalrafael.crm.model.enums.UrgenciaNivel;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "INTERACAO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Interacao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_interacao")
    @SequenceGenerator(name = "sq_interacao", sequenceName = "SQ_INTERACAO", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LEAD_ID", nullable = false)
    private Lead lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERADOR_ID", nullable = false)
    private Usuario operador;

    @Size(max = 40)
    @Column(length = 40)
    private String tipo;

    @Size(max = 2000)
    @Column(length = 2000)
    private String conteudo;

    @Column(name = "URGENCIA_DETECTADA")
    @Builder.Default
    private Boolean urgenciaDetectada = false;

    /** Nível enriquecido pela IA: CRITICO, ALTO, MEDIO, BAIXO, NORMAL */
    @Enumerated(EnumType.STRING)
    @Column(name = "URGENCIA_NIVEL", length = 20)
    @Builder.Default
    private UrgenciaNivel urgenciaNivel = UrgenciaNivel.NORMAL;

    @Column(name = "REALIZADO_EM")
    private LocalDate realizadoEm;

    @PrePersist
    protected void onCreate() {
        this.realizadoEm = LocalDate.now();
        if (this.urgenciaDetectada == null) this.urgenciaDetectada = false;
        if (this.urgenciaNivel == null) this.urgenciaNivel = UrgenciaNivel.NORMAL;
    }
}
