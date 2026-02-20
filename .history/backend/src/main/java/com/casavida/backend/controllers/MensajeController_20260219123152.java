package com.casavida.backend.controllers;

import com.casavida.backend.entity.Mensaje;
import com.casavida.backend.entity.User;
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.MensajeRepository;
import com.casavida.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    MensajeRepository mensajeRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/recibidos")
    public ResponseEntity<List<Mensaje>> getMensajesRecibidos(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).get();
        return ResponseEntity.ok(mensajeRepository.findByDestinatarioOrderByFechaEnvioDesc(user));
    }

    @GetMapping("/enviados")
    public ResponseEntity<List<Mensaje>> getMensajesEnviados(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).get();
        return ResponseEntity.ok(mensajeRepository.findByRemitenteOrderByFechaEnvioDesc(user));
    }

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarMensaje(@RequestBody MensajeRequest request, Authentication authentication) {
        User remitente = userRepository.findByUsername(authentication.getName()).get();
        Optional<User> destinatarioOpt = userRepository.findById(request.getDestinatarioId());

        if (destinatarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Destinatario no encontrado."));
        }

        Mensaje mensaje = new Mensaje(remitente, destinatarioOpt.get(), request.getAsunto(), request.getContenido());
        mensajeRepository.save(mensaje);

        return ResponseEntity.ok(new MessageResponse("Mensaje enviado con éxito."));
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<?> marcarComoLeido(@PathVariable Long id, Authentication authentication) {
        Optional<Mensaje> mensajeOpt = mensajeRepository.findById(id);
        if (mensajeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Mensaje mensaje = mensajeOpt.get();
        User currentUser = userRepository.findByUsername(authentication.getName()).get();

        if (!mensaje.getDestinatario().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body(new MessageResponse("No tienes permiso para leer este mensaje."));
        }

        mensaje.setLeido(true);
        mensajeRepository.save(mensaje);
        return ResponseEntity.ok(new MessageResponse("Mensaje marcado como leído."));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).get();
        return ResponseEntity.ok(mensajeRepository.countByDestinatarioAndLeido(user, false));
    }

    public static class MensajeRequest {
        private Long destinatarioId;
        private String asunto;
        private String contenido;

        public Long getDestinatarioId() { return destinatarioId; }
        public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }
        public String getAsunto() { return asunto; }
        public void setAsunto(String asunto) { this.asunto = asunto; }
        public String getContenido() { return contenido; }
        public void setContenido(String contenido) { this.contenido = contenido; }
    }
}
