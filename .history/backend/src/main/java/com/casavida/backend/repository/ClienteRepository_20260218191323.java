package com.casavida.backend.repository;

import com.casavida.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByNombreContainingOrApellidosContaining(String nombre, String apellidos);

    Optional<Cliente> findByEmail(String email);

    Boolean existsByEmail(String email);
}
