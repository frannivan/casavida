package com.casavida.backend.repository;

import com.casavida.backend.entity.Mensaje;
import com.casavida.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    List<Mensaje> findByDestinatarioOrderByFechaEnvioDesc(User destinatario);
    List<Mensaje> findByRemitenteOrderByFechaEnvioDesc(User remitente);
    long countByDestinatarioAndLeido(User destinatario, boolean leido);
}
