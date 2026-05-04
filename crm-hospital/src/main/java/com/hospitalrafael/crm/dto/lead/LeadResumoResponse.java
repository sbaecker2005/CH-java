package com.hospitalrafael.crm.dto.lead;

import lombok.Data;

@Data
public class LeadResumoResponse {
    private Long id;
    private String nome;
    private String email;
    private String status;
    private String leadScore;
    private Boolean fatorUrgencia;
}
