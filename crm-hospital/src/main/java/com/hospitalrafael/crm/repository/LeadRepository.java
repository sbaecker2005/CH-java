package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Lead;
import com.hospitalrafael.crm.model.enums.StatusLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Optional<Lead> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Lead> findByStatusOrderByPrioridadeAsc(StatusLead status);
    List<Lead> findAllByOrderByPrioridadeAsc();

    @Query("SELECT l FROM Lead l WHERE l.fatorUrgencia = true ORDER BY l.prioridade ASC")
    List<Lead> findLeadsUrgentesOrdenados();
}
