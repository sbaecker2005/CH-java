package com.hospitalrafael.crm.dto.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrgencyAnalysisResponse {
    private boolean urgente;
    /** CRITICO | ALTO | NORMAL */
    private String nivel;
    private String justificativa;
}
