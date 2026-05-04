package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Interacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteracaoRepository extends JpaRepository<Interacao, Long> {
    List<Interacao> findByLeadId(Long leadId);
    List<Interacao> findByOperadorId(Long operadorId);
    List<Interacao> findByUrgenciaDetectadaTrue();
}
