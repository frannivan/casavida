package com.casavida.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for payment response
 * Lightweight response without exposing full entity relationships
 */
public class PagoResponseDTO {
    
    private Long id;
    private Long contratoId;
    private String clienteNombre;
    private BigDecimal monto;
    private LocalDate fechaPago;
    private String referencia;
    private String concepto;
    private String metodoPago;
    private String estatus;
    private boolean hasComprobante;
    private boolean validado;
    private java.time.LocalDateTime fechaValidacion;
    private String validadoPor;

    // Constructors
    public PagoResponseDTO() {
    }

    public PagoResponseDTO(Long id, Long contratoId, BigDecimal monto, LocalDate fechaPago) {
        this.id = id;
        this.contratoId = contratoId;
        this.monto = monto;
        this.fechaPago = fechaPago;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
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

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public boolean isHasComprobante() {
        return hasComprobante;
    }

    public void setHasComprobante(boolean hasComprobante) {
        this.hasComprobante = hasComprobante;
    }

    public boolean isValidado() {
        return validado;
    }

    public void setValidado(boolean validado) {
        this.validado = validado;
    }

    public java.time.LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }

    public void setFechaValidacion(java.time.LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }

    public String getValidadoPor() {
        return validadoPor;
    }

    public void setValidadoPor(String validadoPor) {
        this.validadoPor = validadoPor;
    }
}
