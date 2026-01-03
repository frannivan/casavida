package com.casavida.backend.controllers;

import com.casavida.backend.entity.Cliente;
import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.User;
import com.casavida.backend.payload.response.ClientDashboardResponse;
import com.casavida.backend.payload.response.ClientDashboardResponse.ContratoSummary;
import com.casavida.backend.repository.ClienteRepository;
import com.casavida.backend.repository.ContratoRepository;
import com.casavida.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client")
public class ClientPortalController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ContratoRepository contratoRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        // 1. Get logged-in user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado.");
        }
        User user = userOpt.get();

        // 2. Find Cliente by Email
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(user.getEmail());
        if (clienteOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("No se encontró un perfil de cliente asociado a este correo ("
                    + user.getEmail() + "). Contacte a administración.");
        }
        Cliente cliente = clienteOpt.get();

        // 3. Get Contracts
        List<Contrato> contratos = contratoRepository.findByClienteId(cliente.getId());

        // 4. Map to DTO
        List<ContratoSummary> contratoSummaries = contratos.stream().map(c -> {
            String fraccionamiento = c.getLote().getFraccionamiento() != null
                    ? c.getLote().getFraccionamiento().getNombre()
                    : "Lote Independiente";
            return new ContratoSummary(
                    c.getId(),
                    c.getLote().getNumeroLote(),
                    fraccionamiento,
                    c.getFechaContrato().toString(),
                    c.getEstatus().toString());
        }).collect(Collectors.toList());

        ClientDashboardResponse response = new ClientDashboardResponse(
                cliente.getNombre() + " " + cliente.getApellidos(),
                cliente.getEmail(),
                contratoSummaries);

        return ResponseEntity.ok(response);
    }
}
