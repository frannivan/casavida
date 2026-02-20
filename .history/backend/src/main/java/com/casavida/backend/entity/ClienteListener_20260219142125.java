package com.casavida.backend.entity;

import com.casavida.backend.repository.RoleRepository;
import com.casavida.backend.util.BeanUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.PrePersist;
import java.util.Collections;
import java.util.HashSet;

public class ClienteListener {

    @PrePersist
    public void prePersist(Cliente cliente) {
        if (cliente.getUser() == null) {
            // Automated creation "Trigger"
            PasswordEncoder encoder = BeanUtil.getBean(PasswordEncoder.class);
            RoleRepository roleRepository = BeanUtil.getBean(RoleRepository.class);

            User user = new User();
            user.setUsername(cliente.getEmail());
            user.setEmail(cliente.getEmail());
            
            // Password logic: INE if available, else Phone
            String rawPassword = (cliente.getIne() != null && !cliente.getIne().isEmpty()) 
                    ? cliente.getIne() : cliente.getTelefono();
            
            // Basic validation for headless creation
            if (rawPassword == null || rawPassword.isEmpty()) {
                rawPassword = "CasaVida123";
            }
            
            user.setPassword(encoder.encode(rawPassword));

            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            cliente.setUser(user);
        }
    }
}
