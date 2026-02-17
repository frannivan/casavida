package com.casavida.backend.services;

import java.util.Set;

/**
 * Service for handling RBAC permissions
 */
public interface PermissionService {
    
    /**
     * Get all unique permission keys for a given user
     * @param userId User ID
     * @return Set of permission keys (e.g., "PAGOS.VIEW", "PAGOS.CREATE")
     */
    Set<String> getUserPermissions(Long userId);
    
    /**
     * Check if a user has a specific permission
     * @param userId User ID
     * @param permissionKey Permission key to check
     * @return true if user has permission
     */
    boolean hasPermission(Long userId, String permissionKey);
    
    /**
     * Clear permission cache for a specific user
     * @param userId User ID
     */
    void evictUserPermissions(Long userId);
    
    /**
     * Clear all permission caches
     */
    void evictAllPermissions();
}
