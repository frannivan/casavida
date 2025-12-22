package com.casavida.backend.repository;

import com.casavida.backend.entity.Lote;
import com.casavida.backend.entity.EStatusLote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoteRepository extends JpaRepository<Lote, Long> {
    List<Lote> findByEstatus(EStatusLote estatus);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM Lote l WHERE l.estatus = :estatus " +
            "AND (:fraccionamientoId IS NULL OR l.fraccionamiento.id = :fraccionamientoId) " +
            "AND (:ubicacion IS NULL OR LOWER(l.fraccionamiento.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%')))")
    List<Lote> searchLotes(
            @org.springframework.data.repository.query.Param("estatus") EStatusLote estatus,
            @org.springframework.data.repository.query.Param("fraccionamientoId") Long fraccionamientoId,
            @org.springframework.data.repository.query.Param("ubicacion") String ubicacion,
            org.springframework.data.domain.Sort sort);
}
