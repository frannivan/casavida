package com.casavida.backend.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import com.casavida.backend.entity.Pago;
import com.casavida.backend.entity.Contrato;
import com.casavida.backend.repository.PagoRepository;
import com.casavida.backend.repository.ContratoRepository;
import com.casavida.backend.payload.response.MessageResponse;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    PagoRepository pagoRepository;

    @Autowired
    ContratoRepository contratoRepository;

    @GetMapping("/contrato/{contratoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<Pago> getPagosByContrato(@PathVariable Long contratoId) {
        return pagoRepository.findByContratoId(contratoId);
    }

    // Serve Receipt Image
    @GetMapping("/{id}/comprobante")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<byte[]> getComprobante(@PathVariable Long id) {
        Pago pago = pagoRepository.findById(id).orElse(null);
        if (pago == null || pago.getComprobante() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, pago.getComprobanteContentType())
                .body(pago.getComprobante());
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarPago(
            @RequestParam("contratoId") Long contratoId,
            @RequestParam("monto") BigDecimal monto,
            @RequestParam(value = "fechaPago", required = false) String fechaPagoStr,
            @RequestParam(value = "referencia", required = false) String referencia,
            @RequestParam(value = "concepto", required = false) String concepto,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        Contrato contrato = contratoRepository.findById(contratoId)
                .orElseThrow(() -> new RuntimeException("Error: Contrato no encontrado."));

        Pago pago = new Pago();
        pago.setContrato(contrato);
        pago.setMonto(monto);
        pago.setReferencia(referencia);
        pago.setConcepto(concepto);

        if (fechaPagoStr != null && !fechaPagoStr.isEmpty()) {
            pago.setFechaPago(LocalDate.parse(fechaPagoStr));
        } else {
            pago.setFechaPago(LocalDate.now());
        }

        if (file != null && !file.isEmpty()) {
            try {
                pago.setComprobante(file.getBytes());
                pago.setComprobanteContentType(file.getContentType());
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error al subir imagen."));
            }
        }

        pagoRepository.save(pago);
        return ResponseEntity.ok(new MessageResponse("Pago registrado exitosamente."));
    }
}
