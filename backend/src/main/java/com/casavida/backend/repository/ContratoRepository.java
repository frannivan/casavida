package com.casavida.backend.repository;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.EStatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContratoRepository extends JpaRepository<Contrato, Long> {
    List<Contrato> findByEstatus(EStatusContrato estatus);

    List<Contrato> findByClienteId(Long clienteId);

    List<Contrato> findByLoteId(Long loteId);

    List<Contrato> findByVendedor(com.casavida.backend.entity.User vendedor);

    List<Contrato> findByClienteEmail(String email);
}
