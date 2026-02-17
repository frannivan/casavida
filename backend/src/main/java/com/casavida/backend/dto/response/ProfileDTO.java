package com.casavida.backend.dto.response;

import java.util.List;
import java.util.Map;

public class ProfileDTO {
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private Map<String, Object> extraData;

    public ProfileDTO() {
    }

    public ProfileDTO(Long id, String username, String email, List<String> roles, List<String> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Map<String, Object> getExtraData() {
        return extraData;
    }

    public void setExtraData(Map<String, Object> extraData) {
        this.extraData = extraData;
    }
}
