package com.casavida.backend.services;

import com.casavida.backend.dto.UserDTO;
import com.casavida.backend.entity.ERole;
import com.casavida.backend.entity.Role;
import com.casavida.backend.entity.User;
import com.casavida.backend.repository.RoleRepository;
import com.casavida.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> {
            String roleName = user.getRoles().stream()
                    .findFirst()
                    .map(role -> role.getName().name().replace("ROLE_", ""))
                    .orElse("USER");
            return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), roleName);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createUser(UserDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya existe");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        ERole roleEnum;
        try {
            roleEnum = ERole.valueOf("ROLE_" + request.getRole());
        } catch (IllegalArgumentException e) {
            roleEnum = ERole.ROLE_USER;
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Transactional
    public void updateUser(Long id, UserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Set<Role> roles = new HashSet<>();
        ERole roleEnum;
        try {
            roleEnum = ERole.valueOf("ROLE_" + request.getRole());
        } catch (IllegalArgumentException e) {
            roleEnum = ERole.ROLE_USER;
        }

        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
