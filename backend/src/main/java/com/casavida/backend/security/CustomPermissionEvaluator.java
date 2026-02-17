package com.casavida.backend.security;

import com.casavida.backend.security.services.UserDetailsImpl;
import com.casavida.backend.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Custom Permission Evaluator for RBAC
 * Enables usage of hasPermission('key') in @PreAuthorize annotations
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if (auth == null || !auth.isAuthenticated() || !(permission instanceof String)) {
            return false;
        }
        
        Object principal = auth.getPrincipal();
        if (!(principal instanceof UserDetailsImpl)) {
            return false;
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String permissionKey = (String) permission;
        
        return permissionService.hasPermission(userDetails.getId(), permissionKey);
    }
    
    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        // We typically use the 3-argument version with null for targetDomainObject
        return hasPermission(auth, null, permission);
    }
}
