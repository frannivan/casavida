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
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.ClienteRepository;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    com.casavida.backend.repository.ContratoRepository contratoRepository;

    @GetMapping("/{id}/contratos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<com.casavida.backend.entity.Contrato> getContratosByCliente(@PathVariable Long id) {
        return contratoRepository.findByClienteId(id);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cliente> getAllClientes() {
        return clienteRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getClienteById(@PathVariable Long id) {
        return clienteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCliente(@RequestBody Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está registrado en otro cliente."));
        }
        clienteRepository.save(cliente);
        return ResponseEntity.ok(new MessageResponse("Cliente registrado exitosamente."));
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
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Faltan datos requeridos."));
        }

        // Allow public leads to bypass strict checks or fill dummy data if needed
        if (cliente.getApellidos() == null)
            cliente.setApellidos("-"); // Placeholder if not provided

        clienteRepository.save(cliente);
        return ResponseEntity.ok(new MessageResponse("Solicitud recibida. Un asesor se pondrá en contacto contigo."));
    }
}
