package com.hospitalrafael.crm.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Relatório diário gerado pela IA às 7h.
 * Contém análise do CRM com insights e recomendações.
 */
@Entity
@Table(name = "RELATORIO_IA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RelatorioIa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_relatorio_ia")
    @SequenceGenerator(name = "sq_relatorio_ia", sequenceName = "SQ_RELATORIO_IA", allocationSize = 1)
    private Long id;

    @Column(name = "DATA_REFERENCIA", nullable = false)
    private LocalDate dataReferencia;

    @Column(name = "GERADO_EM", nullable = false)
    private LocalDateTime geradoEm;

    @Column(name = "TOTAL_LEADS")
    private Integer totalLeads;

    @Column(name = "LEADS_NOVOS")
    private Integer leadsNovos;

    @Column(name = "LEADS_CONVERTIDOS")
    private Integer leadsConvertidos;

    @Column(name = "LEADS_URGENTES")
    private Integer leadsUrgentes;

    @Column(name = "TAXA_CONVERSAO")
    private Double taxaConversao;

    @Lob
    @Column(name = "RESUMO_IA")
    private String resumoIa;

    @Lob
    @Column(name = "RECOMENDACOES_IA")
    private String recomendacoesIa;

    @PrePersist
    protected void onCreate() {
        if (this.geradoEm == null) this.geradoEm = LocalDateTime.now();
    }
}
