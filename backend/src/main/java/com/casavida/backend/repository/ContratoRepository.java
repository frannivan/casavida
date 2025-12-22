package com.casavida.backend.repository;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.EStatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByEstatus(EStatusContrato estatus);

    List<Contrato> findByClienteId(Long clienteId);
}
