package com.casavida.backend.repository;

import com.casavida.backend.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByContratoId(Long contratoId);
}
