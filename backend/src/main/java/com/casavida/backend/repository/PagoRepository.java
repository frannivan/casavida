package com.casavida.backend.repository;

import com.casavida.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByContratoId(Long contratoId);

    /**
     * Find all payments for contracts assigned to a specific vendor
     * @param vendedorId Vendor/Salesperson user ID
     * @return List of payments
     */
    List<Pago> findByContratoVendedorId(Long vendedorId);
    
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Pago p WHERE p.contrato.lote.fraccionamiento.id = :fraccionamientoId")
    List<Pago> findByFraccionamientoId(@org.springframework.data.repository.query.Param("fraccionamientoId") Long fraccionamientoId);
}
