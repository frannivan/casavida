package com.casavida.backend.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.Cliente;
import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.EStatusContrato;
import com.casavida.backend.entity.EStatusLote;
import com.casavida.backend.entity.Lote;
// Conceptually useful, but logic handled in Contrato creation
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.ClienteRepository;
import com.casavida.backend.repository.ContratoRepository;
import com.casavida.backend.repository.LoteRepository;
import com.casavida.backend.services.CreditService;
import com.casavida.backend.services.CreditService.AmortizationRow;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    CreditService creditService;

    @Autowired
    ContratoRepository contratoRepository;

    @Autowired
    LoteRepository loteRepository;

    @Autowired
    ClienteRepository clienteRepository;

    public static class CotizacionRequest {
        private BigDecimal montoTotal;
        private BigDecimal enganche;
        private int plazoMeses;
        private BigDecimal tasaAnual;

        public BigDecimal getMontoTotal() {
            return montoTotal;
        }

        public void setMontoTotal(BigDecimal montoTotal) {
            this.montoTotal = montoTotal;
        }

        public BigDecimal getEnganche() {
            return enganche;
        }

        public void setEnganche(BigDecimal enganche) {
            this.enganche = enganche;
        }

        public int getPlazoMeses() {
            return plazoMeses;
        }

        public void setPlazoMeses(int plazoMeses) {
            this.plazoMeses = plazoMeses;
        }

        public BigDecimal getTasaAnual() {
            return tasaAnual;
        }

        public void setTasaAnual(BigDecimal tasaAnual) {
            this.tasaAnual = tasaAnual;
        }
    }

    public static class ContratoRequest {
        private Long clienteId;
        private Long loteId;
        private BigDecimal montoTotal;
        private BigDecimal enganche;
        private int plazoMeses;
        private BigDecimal tasaAnual;

        public Long getClienteId() {
            return clienteId;
        }

        public void setClienteId(Long clienteId) {
            this.clienteId = clienteId;
        }

        public Long getLoteId() {
            return loteId;
        }

        public void setLoteId(Long loteId) {
            this.loteId = loteId;
        }

        public BigDecimal getMontoTotal() {
            return montoTotal;
        }

        public void setMontoTotal(BigDecimal montoTotal) {
            this.montoTotal = montoTotal;
        }

        public BigDecimal getEnganche() {
            return enganche;
        }

        public void setEnganche(BigDecimal enganche) {
            this.enganche = enganche;
        }

        public int getPlazoMeses() {
            return plazoMeses;
        }

        public void setPlazoMeses(int plazoMeses) {
            this.plazoMeses = plazoMeses;
        }

        public BigDecimal getTasaAnual() {
            return tasaAnual;
        }

        public void setTasaAnual(BigDecimal tasaAnual) {
            this.tasaAnual = tasaAnual;
        }
    }

    @PostMapping("/cotizar")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> cotizar(@RequestBody CotizacionRequest request) {
        BigDecimal montoPrestamo = request.getMontoTotal().subtract(request.getEnganche());
        if (montoPrestamo.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.ok("Venta de Contado - Sin tabla de amortización.");
        }

        List<AmortizationRow> tabla = creditService.calculateAmortization(montoPrestamo, request.getPlazoMeses(),
                request.getTasaAnual());
        return ResponseEntity.ok(tabla);
    }

    @PostMapping("/contratar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> crearContrato(@RequestBody ContratoRequest request) {
        Lote lote = loteRepository.findById(request.getLoteId())
                .orElseThrow(() -> new RuntimeException("Error: Lote no encontrado."));

        if (lote.getEstatus() != EStatusLote.DISPONIBLE) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: El lote no está disponible."));
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Error: Cliente no encontrado."));

        Contrato contrato = new Contrato();
        contrato.setCliente(cliente);
        contrato.setLote(lote);
        contrato.setFechaContrato(LocalDate.now());
        contrato.setMontoTotal(request.getMontoTotal());
        contrato.setEnganche(request.getEnganche());
        contrato.setPlazoMeses(request.getPlazoMeses());

        // Convert BigDecimal to Double for this specific field if entity requires
        // Double,
        // or check if Entity should use BigDecimal. Contrato.java uses Double for
        // tasaInteresAnual.
        if (request.getTasaAnual() != null) {
            contrato.setTasaInteresAnual(request.getTasaAnual().doubleValue());
        }

        // Calcular cuota mensual promedio (referencial)
        if (request.getPlazoMeses() > 0) {
            // Placeholder logic
            contrato.setMensualidad(BigDecimal.ZERO);
        } else {
            contrato.setMensualidad(BigDecimal.ZERO);
        }

        contrato.setEstatus(EStatusContrato.ACTIVO);

        contratoRepository.save(contrato);

        // Actualizar estatus del lote
        lote.setEstatus(EStatusLote.CONTRATADO);
        loteRepository.save(lote);

        return ResponseEntity.ok(new MessageResponse("Contrato generado exitosamente. ID: " + contrato.getId()));
    }

    @GetMapping("/contratos")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Contrato> getAllContratos() {
        return contratoRepository.findAll();
    }
}
