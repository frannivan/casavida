package com.casavida.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.Cliente;
import com.casavida.backend.entity.ERole;
import com.casavida.backend.entity.Role;
import com.casavida.backend.entity.User;
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.ClienteRepository;
import com.casavida.backend.repository.RoleRepository;
import com.casavida.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Collections;
import java.util.HashSet;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    com.casavida.backend.repository.ContratoRepository contratoRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping("/{id}/contratos")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public List<com.casavida.backend.entity.Contrato> getContratosByCliente(@PathVariable Long id) {
        return contratoRepository.findByClienteId(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está registrado en otro cliente.");
        }

        // Create User account for the client
        User user = new User();
        user.setUsername(cliente.getEmail());
        user.setEmail(cliente.getEmail());
        // Use INE as initial password if provided, otherwise use phone number
        String rawPassword = (cliente.getIne() != null && !cliente.getIne().isEmpty()) ? cliente.getIne() : cliente.getTelefono();
        user.setPassword(encoder.encode(rawPassword));

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        // Link User to Cliente
        cliente.setUser(user);

        clienteRepository.save(cliente);
        return ResponseEntity.ok(new MessageResponse("Cliente y cuenta de usuario registrados exitosamente."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCliente(@PathVariable Long id, @RequestBody Cliente clienteDetails) {
        return clienteRepository.findById(id).map(cliente -> {
            cliente.setNombre(clienteDetails.getNombre());
            cliente.setApellidos(clienteDetails.getApellidos());
            cliente.setTelefono(clienteDetails.getTelefono());
            cliente.setDireccion(clienteDetails.getDireccion());
            cliente.setIne(clienteDetails.getIne());
            clienteRepository.save(cliente);
            return ResponseEntity.ok(new MessageResponse("Cliente actualizado exitosamente."));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/public/lead")
    public ResponseEntity<?> registerLead(@RequestBody Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity
                    .ok(new MessageResponse("Gracias. Ya tenemos tus datos, un asesor te contactará pronto."));
        }

        // Basic validation or defaults
        if (cliente.getNombre() == null || cliente.getEmail() == null || cliente.getTelefono() == null) {
            throw new IllegalArgumentException("Error: Faltan datos requeridos.");
        }

        // Allow public leads to bypass strict checks or fill dummy data if needed
        if (cliente.getApellidos() == null)
            cliente.setApellidos("-"); // Placeholder if not provided

        // Create User account for the lead
        User user = new User();
        user.setUsername(cliente.getEmail());
        user.setEmail(cliente.getEmail());
        // For public leads, we might not have INE yet, use a default or phone
        String rawPassword = (cliente.getIne() != null && !cliente.getIne().isEmpty()) ? cliente.getIne() : cliente.getTelefono();
        user.setPassword(encoder.encode(rawPassword));

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        cliente.setUser(user);

        clienteRepository.save(cliente);
        return ResponseEntity.ok(new MessageResponse("Solicitud recibida. Se ha creado tu cuenta con tu correo y teléfono como contraseña."));
    }
}
