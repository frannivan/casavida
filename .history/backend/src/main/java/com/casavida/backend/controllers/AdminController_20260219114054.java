package com.casavida.backend.controllers;

import com.casavida.backend.dto.UserDTO;
import com.casavida.backend.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(adminService.getAllUsers());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new com.casavida.backend.payload.response.MessageResponse("Backend Error: " + e.getClass().getName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserDTO request) {
        try {
            adminService.createUser(request);
            return ResponseEntity.ok("User created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO request) {
        try {
            adminService.updateUser(id, request);
            return ResponseEntity.ok("User updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
            @GetMapping("/db-roles")
    public ResponseEntity<?> getDbRoles() {
        try {
            javax.persistence.Query query = adminService.getEntityManager().createNativeQuery("SELECT name FROM roles");
            return ResponseEntity.ok(query.getResultList());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
} catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user");
        }
    }
}
