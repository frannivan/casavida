package com.casavida.backend.services.impl;

import com.casavida.backend.entity.Permission;
import com.casavida.backend.entity.Role;
import com.casavida.backend.entity.User;
import com.casavida.backend.repository.PermissionRepository;
import com.casavida.backend.repository.UserRepository;
import com.casavida.backend.services.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of PermissionService
 * Handles user permission retrieval and checking with optional caching
 */
@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Override
    @Cacheable(value = "userPermissions", key = "#userId")
    public Set<String> getUserPermissions(Long userId) {
        logger.debug("Retrieving permissions for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElse(null);
        
        if (user == null) {
            logger.warn("User not found for permission check: {}", userId);
            return new HashSet<>();
        }
        
        Set<String> permissions = new HashSet<>();
        
        // Collect permissions from all roles the user has
        for (Role role : user.getRoles()) {
            List<Permission> rolePerms = permissionRepository.findByRoleId(role.getId());
            permissions.addAll(rolePerms.stream()
                    .map(Permission::getPermissionKey)
                    .collect(Collectors.toSet()));
        }
        
        logger.debug("User {} has {} permissions", userId, permissions.size());
        return permissions;
    }
    
    @Override
    public boolean hasPermission(Long userId, String permissionKey) {
        // This method gets user ID (Long), but we need to check permissions via Role (Integer ID)
        Set<String> userPermissions = getUserPermissions(userId);
        return userPermissions.contains(permissionKey);
    }
    
    @Override
    @CacheEvict(value = "userPermissions", key = "#userId")
    public void evictUserPermissions(Long userId) {
        logger.info("Evicting permissions cache for user ID: {}", userId);
    }
    
    @Override
    @CacheEvict(value = "userPermissions", allEntries = true)
    public void evictAllPermissions() {
        logger.info("Evicting all permissions cache");
    }
}
