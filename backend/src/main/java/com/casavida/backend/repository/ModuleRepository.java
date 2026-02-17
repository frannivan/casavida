package com.casavida.backend.repository;

import com.casavida.backend.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Module entity
 * Handles database operations for RBAC modules
 */
@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    
    /**
     * Find module by name
     * @param name Module name
     * @return Optional module
     */
    Optional<Module> findByName(String name);
    
    /**
     * Find all active modules ordered by display order
     * @return List of active modules
     */
    List<Module> findByActiveTrueOrderByDisplayOrderAsc();
}
