package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByLeadId(Long leadId);
    List<Notificacao> findByOperadorId(Long operadorId);
    List<Notificacao> findByLidaFalse();
    long countByLidaFalse();
}
