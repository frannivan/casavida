package com.casavida.backend.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * DTO for payment validation request
 * Used to validate/approve payments
 */
public class PagoValidacionDTO {
    
    @NotBlank(message = "Estatus es requerido")
    @Pattern(regexp = "PENDIENTE|VALIDADO|RECHAZADO", 
             message = "Estatus debe ser: PENDIENTE, VALIDADO o RECHAZADO")
    private String estatus;
    
    private String comentarios;

    // Constructors
    public PagoValidacionDTO() {
    }

    public PagoValidacionDTO(String estatus) {
        this.estatus = estatus;
    }

    public PagoValidacionDTO(String estatus, String comentarios) {
        this.estatus = estatus;
        this.comentarios = comentarios;
    }

    // Getters and Setters
    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
