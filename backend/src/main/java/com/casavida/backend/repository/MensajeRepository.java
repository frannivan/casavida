package com.casavida.backend.repository;

import com.casavida.backend.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Mensaje (CRM Communication).
 * Provee consultas por target (Lead/Oportunidad) y tipo de canal.
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByTargetIdOrderByFechaAsc(Long targetId);
    List<Mensaje> findByTargetIdAndTipoOrderByFechaAsc(Long targetId, String tipo);
}
