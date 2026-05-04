package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.Agendamento;
import com.hospitalrafael.crm.model.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByLeadId(Long leadId);
    List<Agendamento> findByOperadorId(Long operadorId);
    List<Agendamento> findByStatus(StatusAgendamento status);
    List<Agendamento> findByLembreteEnviadoFalse();
    boolean existsByOperadorIdAndDataHora(Long operadorId, LocalDateTime dataHora);

    @Query("SELECT a FROM Agendamento a WHERE CAST(a.dataHora AS LocalDate) = :data")
    List<Agendamento> findByData(LocalDate data);
}
