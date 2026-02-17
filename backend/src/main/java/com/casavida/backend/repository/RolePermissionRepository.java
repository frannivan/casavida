package com.casavida.backend.repository;

import com.casavida.backend.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for RolePermission entity
 * Handles mapping between roles and permissions
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {
    
    /**
     * Find all mappings for a specific role
     * @param roleId Role ID
     * @return List of role-permission mappings
     */
    List<RolePermission> findByRoleId(Long roleId);
    
    /**
     * Find all mappings for a specific permission
     * @param permissionId Permission ID
     * @return List of role-permission mappings
     */
    List<RolePermission> findByPermissionId(Long permissionId);
}
