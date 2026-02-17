package com.casavida.backend.dto.request;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for payment registration request
 * Used to create new payments with proper validation
 */
public class PagoRegistroDTO {
    
    @NotNull(message = "Contrato ID es requerido")
    @Positive(message = "Contrato ID debe ser positivo")
    private Long contratoId;
    
    @NotNull(message = "Monto es requerido")
    @DecimalMin(value = "0.01", message = "Monto debe ser mayor a 0")
    private BigDecimal monto;
    
    @Past(message = "Fecha de pago no puede ser en el futuro")
    private LocalDate fechaPago;
    
    @Size(max = 255, message = "Referencia no puede exceder 255 caracteres")
    private String referencia;
    
    @Size(max = 500, message = "Concepto no puede exceder 500 caracteres")
    private String concepto;
    
    @NotNull(message = "Método de pago es requerido")
    @Pattern(regexp = "Efectivo|Transferencia|Cheque|Tarjeta", 
             message = "Método de pago debe ser: Efectivo, Transfer encia, Cheque o Tarjeta")
    private String metodoPago;

    // Constructors
    public PagoRegistroDTO() {
    }

    public PagoRegistroDTO(Long contratoId, BigDecimal monto, LocalDate fechaPago, 
                          String referencia, String concepto, String metodoPago) {
        this.contratoId = contratoId;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.referencia = referencia;
        this.concepto = concepto;
        this.metodoPago = metodoPago;
    }

    // Getters and Setters
    public Long getContratoId() {
        return contratoId;
    }

    public void setContratoId(Long contratoId) {
        this.contratoId = contratoId;
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
}
