package com.casavida.backend.controllers;

import com.casavida.backend.entity.Mensaje;
import com.casavida.backend.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST unificado de mensajería.
 * Soporta comunicación CRM (WhatsApp/Email con Leads) y
 * mensajería interna entre usuarios del sistema.
 *
 * @see Mensaje
 * @since CU03/CU04 – Gestión de Comunicación
 */
@RestController
@RequestMapping("/api/mensajes")
@CrossOrigin(origins = "*")
public class MensajeController {

    @Autowired
    private MensajeRepository mensajeRepository;

    // ═══════════════════════════════════════════════════════
    //  Internal Messaging Endpoints (static routes FIRST)
    // ═══════════════════════════════════════════════════════

    @GetMapping("/recibidos")
    public ResponseEntity<List<Mensaje>> getRecibidos() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/enviados")
    public ResponseEntity<List<Mensaje>> getEnviados() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/no-leidos/count")
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(0L);
    }

    @PostMapping("/enviar")
    public ResponseEntity<Mensaje> enviarMensaje(@RequestBody Map<String, Object> payload) {
        Mensaje m = new Mensaje();
        m.setTargetId(0L);
        m.setTipo("EMAIL");
        m.setDireccion("ENVIADO");
        m.setContenido((String) payload.getOrDefault("contenido", ""));
        m.setRemitente("Sistema");
        return ResponseEntity.ok(mensajeRepository.save(m));
    }

    @PutMapping("/{id}/leido")
    public ResponseEntity<Void> marcarComoLeido(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }

    // ═══════════════════════════════════════════════════════
    //  CRM Communication Endpoints (dynamic routes AFTER)
    // ═══════════════════════════════════════════════════════

    @GetMapping("/{targetId}")
    public ResponseEntity<List<Mensaje>> getHistory(@PathVariable Long targetId) {
        List<Mensaje> history = mensajeRepository.findByTargetIdOrderByFechaAsc(targetId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{targetId}/{tipo}")
    public ResponseEntity<List<Mensaje>> getHistoryByType(
            @PathVariable Long targetId,
            @PathVariable String tipo) {
        List<Mensaje> history = mensajeRepository.findByTargetIdAndTipoOrderByFechaAsc(targetId, tipo.toUpperCase());
        return ResponseEntity.ok(history);
    }

    @PostMapping
    public ResponseEntity<Mensaje> createMessage(@RequestBody Mensaje mensaje) {
        Mensaje saved = mensajeRepository.save(mensaje);
        return ResponseEntity.ok(saved);
    }
}
