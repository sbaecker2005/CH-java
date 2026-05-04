package com.hospitalrafael.crm.repository;

import com.hospitalrafael.crm.model.RelatorioIa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RelatorioIaRepository extends JpaRepository<RelatorioIa, Long> {
    Optional<RelatorioIa> findTopByOrderByGeradoEmDesc();
    Optional<RelatorioIa> findByDataReferencia(LocalDate data);
}
