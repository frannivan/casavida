package com.casavida.backend.repository;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.EStatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByEstatus(EStatusContrato estatus);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Contrato c LEFT JOIN FETCH c.pagos WHERE c.cliente.id = :clienteId")
    List<Contrato> findByClienteId(@org.springframework.web.bind.annotation.PathVariable("clienteId") Long clienteId);
}
