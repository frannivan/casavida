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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.Pago;
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.ContratoRepository;
import com.casavida.backend.repository.PagoRepository;

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

    public static class PagoRequest {
        private Long contratoId;
        private LocalDate fechaPago; // Jackson parses "YYYY-MM-DD"
        private BigDecimal monto;
        private String referencia;
        private String concepto;

        public Long getContratoId() {
            return contratoId;
        }

        public void setContratoId(Long contratoId) {
            this.contratoId = contratoId;
        }

        public LocalDate getFechaPago() {
            return fechaPago;
        }

        public void setFechaPago(LocalDate fechaPago) {
            this.fechaPago = fechaPago;
        }

        public BigDecimal getMonto() {
            return monto;
        }

        public void setMonto(BigDecimal monto) {
            this.monto = monto;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getConcepto() {
            return concepto;
        }

        public void setConcepto(String concepto) {
            this.concepto = concepto;
        }
    }

    @PostMapping("/registrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarPago(@RequestBody PagoRequest request) {
        Contrato contrato = contratoRepository.findById(request.getContratoId())
                .orElseThrow(() -> new RuntimeException("Error: Contrato no encontrado."));

        Pago pago = new Pago();
        pago.setContrato(contrato);
        pago.setMonto(request.getMonto());
        pago.setReferencia(request.getReferencia());
        pago.setConcepto(request.getConcepto());
        pago.setFechaPago(request.getFechaPago() != null ? request.getFechaPago() : LocalDate.now());

        pagoRepository.save(pago);
        return ResponseEntity.ok(new MessageResponse("Pago registrado exitosamente."));
    }
}
