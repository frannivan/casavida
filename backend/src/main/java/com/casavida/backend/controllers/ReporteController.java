package com.casavida.backend.controllers;

import com.casavida.backend.entity.Contrato;
import com.casavida.backend.entity.Pago;
import com.casavida.backend.repository.ContratoRepository;
import com.casavida.backend.repository.PagoRepository;
import com.casavida.backend.services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

        @Autowired
        ContratoRepository contratoRepository;

        @Autowired
        PagoRepository pagoRepository;

        @Autowired
        PdfService pdfService;

        @Autowired
        com.casavida.backend.repository.LoteRepository loteRepository;

        @Autowired
        com.casavida.backend.repository.ClienteRepository clienteRepository;

        @Autowired
        com.casavida.backend.repository.LeadRepository leadRepository;

        @Autowired
        com.casavida.backend.repository.OpportunityRepository opportunityRepository;

        @GetMapping("/dashboard")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<?> getDashboardStats() {
                long totalLotes = loteRepository.count();
                int lotesDisponibles = loteRepository.findByEstatus(com.casavida.backend.entity.EStatusLote.DISPONIBLE)
                                .size();
                int lotesVendidos = loteRepository.findByEstatus(com.casavida.backend.entity.EStatusLote.VENDIDO)
                                .size();
                long totalClientes = clienteRepository.count();
                long totalContratos = contratoRepository.count();
                long totalLeads = leadRepository.count();
                long totalOpportunities = opportunityRepository.count();

                // Financial Metrics
                java.math.BigDecimal ingresosTotales = pagoRepository.findAll().stream()
                                .map(Pago::getMonto)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                java.math.BigDecimal totalContratosMonto = contratoRepository.findAll().stream()
                                .map(Contrato::getMontoTotal)
                                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

                java.math.BigDecimal saldoPendienteTotal = totalContratosMonto.subtract(ingresosTotales);

                // Recent Sales (Last 5 contracts)
                List<VentaRecienteDTO> ventasRecientes = contratoRepository
                                .findAll(org.springframework.data.domain.Sort.by(
                                                org.springframework.data.domain.Sort.Direction.DESC,
                                                "fechaContrato"))
                                .stream()
                                .limit(5)
                                .map(c -> new VentaRecienteDTO(
                                                c.getLote().getNumeroLote(),
                                                c.getCliente().getNombre() + " " + c.getCliente().getApellidos(),
                                                c.getFechaContrato(),
                                                c.getMontoTotal()))
                                .collect(java.util.stream.Collectors.toList());

                return ResponseEntity.ok(new DashboardStats(
                                totalLotes,
                                lotesDisponibles,
                                lotesVendidos,
                                totalClientes,
                                totalContratos,
                                totalLeads,
                                totalOpportunities,
                                ingresosTotales,
                                saldoPendienteTotal,
                                ventasRecientes));
        }

        @GetMapping("/estado-cuenta/{contratoId}")
        @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
        public ResponseEntity<InputStreamResource> reporteEstadoCuenta(@PathVariable Long contratoId) {
                Contrato contrato = contratoRepository.findById(contratoId)
                                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

                List<Pago> pagos = pagoRepository.findByContratoId(contratoId);

                ByteArrayInputStream bis = pdfService.generateEstadoCuenta(contrato, pagos);

                HttpHeaders headers = new HttpHeaders();
                headers.add("Content-Disposition", "inline; filename=estado_cuenta_" + contratoId + ".pdf");

                return ResponseEntity
                                .ok()
                                .headers(headers)
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(new InputStreamResource(bis));
        }

        // Inner DTO Class
        @lombok.Data
        @lombok.AllArgsConstructor
        static class DashboardStats {
                private long totalLotes;
                private int lotesDisponibles;
                private int lotesVendidos;
                private long totalClientes;
                private long totalContratos;
                private long totalLeads;
                private long totalOpportunities;
                private java.math.BigDecimal ingresosTotales;
                private java.math.BigDecimal saldoPendienteTotal;
                private List<VentaRecienteDTO> ventasRecientes;

                public DashboardStats(long totalLotes, int lotesDisponibles, int lotesVendidos, long totalClientes,
                                long totalContratos, long totalLeads, long totalOpportunities,
                                java.math.BigDecimal ingresosTotales, java.math.BigDecimal saldoPendienteTotal,
                                List<VentaRecienteDTO> ventasRecientes) {
                        this.totalLotes = totalLotes;
                        this.lotesDisponibles = lotesDisponibles;
                        this.lotesVendidos = lotesVendidos;
                        this.totalClientes = totalClientes;
                        this.totalContratos = totalContratos;
                        this.totalLeads = totalLeads;
                        this.totalOpportunities = totalOpportunities;
                        this.ingresosTotales = ingresosTotales;
                        this.saldoPendienteTotal = saldoPendienteTotal;
                        this.ventasRecientes = ventasRecientes;
                }

                public long getTotalLotes() {
                        return totalLotes;
                }

                public int getLotesDisponibles() {
                        return lotesDisponibles;
                }

                public int getLotesVendidos() {
                        return lotesVendidos;
                }

                public long getTotalClientes() {
                        return totalClientes;
                }

                public long getTotalContratos() {
                        return totalContratos;
                }

                public long getTotalLeads() {
                        return totalLeads;
                }

                public long getTotalOpportunities() {
                        return totalOpportunities;
                }

                public java.math.BigDecimal getIngresosTotales() {
                        return ingresosTotales;
                }

                public java.math.BigDecimal getSaldoPendienteTotal() {
                        return saldoPendienteTotal;
                }

                public List<VentaRecienteDTO> getVentasRecientes() {
                        return ventasRecientes;
                }
        }

        @lombok.Data
        @lombok.AllArgsConstructor
        static class VentaRecienteDTO {
                private String lote;
                private String cliente;
                private java.time.LocalDate fecha;
                private java.math.BigDecimal monto;

                public VentaRecienteDTO(String lote, String cliente, java.time.LocalDate fecha,
                                java.math.BigDecimal monto) {
                        this.lote = lote;
                        this.cliente = cliente;
                        this.fecha = fecha;
                        this.monto = monto;
                }

                public String getLote() {
                        return lote;
                }

                public String getCliente() {
                        return cliente;
                }

                public java.time.LocalDate getFecha() {
                        return fecha;
                }

                public java.math.BigDecimal getMonto() {
                        return monto;
                }
        }
}
