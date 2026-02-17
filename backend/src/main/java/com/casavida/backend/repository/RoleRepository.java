package com.casavida.backend.repository;

import com.casavida.backend.entity.Role;
import com.casavida.backend.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
