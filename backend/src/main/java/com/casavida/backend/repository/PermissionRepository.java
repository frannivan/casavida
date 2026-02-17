package com.casavida.backend.repository;

import com.casavida.backend.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Permission entity
 * Handles database operations for permissions
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * Find permission by its unique key
     * @param permissionKey The permission key (e.g., "PAGOS.VIEW")
     * @return Optional permission
     */
    Optional<Permission> findByPermissionKey(String permissionKey);
    
    /**
     * Find all permissions for a specific module
     * @param moduleId Module ID
     * @return List of permissions
     */
    List<Permission> findByModuleId(Long moduleId);
    
    /**
     * Find all permissions for a specific role
     * @param roleId Role ID
     * @return List of permissions granted to this role
     */
    @Query("SELECT rp.permission FROM RolePermission rp WHERE rp.role.id = :roleId AND rp.permission.active = true")
    List<Permission> findByRoleId(@Param("roleId") Integer roleId);
    
    /**
     * Find all active permissions
     * @return List of active permissions
     */
    List<Permission> findByActiveTrue();
    
    /**
     * Check if a role has a specific permission
     * @param roleId Role ID
     * @param permissionKey Permission key
     * @return true if role has permission
     */
    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp " +
           "WHERE rp.role.id = :roleId " +
           "AND rp.permission.permissionKey = :permissionKey " +
           "AND rp.permission.active = true")
    boolean hasPermission(@Param("roleId") Integer roleId, @Param("permissionKey") String permissionKey);
}
