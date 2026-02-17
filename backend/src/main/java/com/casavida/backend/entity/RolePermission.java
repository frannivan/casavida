package com.casavida.backend.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * RolePermission entity for RBAC
 * Maps roles to permissions (many-to-many relationship)
 */
@Entity
@Table(name = "role_permissions")
public class RolePermission implements Serializable {
    
    @EmbeddedId
    private RolePermissionId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;
    
    @Column(name = "granted_at", nullable = false, updatable = false)
    private LocalDateTime grantedAt;
    
    @Column(name = "granted_by", length = 100)
    private String grantedBy;
    
    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }
    
    // Constructors
    public RolePermission() {
    }
    
    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
        this.id = new RolePermissionId(role.getId().longValue(), permission.getId());
    }
    
    public RolePermission(Role role, Permission permission, String grantedBy) {
        this(role, permission);
        this.grantedBy = grantedBy;
    }
    
    // Getters and Setters
    public RolePermissionId getId() {
        return id;
    }
    
    public void setId(RolePermissionId id) {
        this.id = id;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }
    
    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }
    
    public String getGrantedBy() {
        return grantedBy;
    }
    
    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    /**
     * Composite primary key for RolePermission
     */
    @Embeddable
    public static class RolePermissionId implements Serializable {
        
        @Column(name = "role_id")
        private Long roleId;
        
        @Column(name = "permission_id")
        private Long permissionId;
        
        public RolePermissionId() {
        }
        
        public RolePermissionId(Long roleId, Long permissionId) {
            this.roleId = roleId;
            this.permissionId = permissionId;
        }
        
        // Getters and Setters
        public Long getRoleId() {
            return roleId;
        }
        
        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }
        
        public Long getPermissionId() {
            return permissionId;
        }
        
        public void setPermissionId(Long permissionId) {
            this.permissionId = permissionId;
        }
        
        // equals and hashCode
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RolePermissionId)) return false;
            RolePermissionId that = (RolePermissionId) o;
            return roleId.equals(that.roleId) && permissionId.equals(that.permissionId);
        }
        
        @Override
        public int hashCode() {
            return 31 * roleId.hashCode() + permissionId.hashCode();
        }
    }
}
