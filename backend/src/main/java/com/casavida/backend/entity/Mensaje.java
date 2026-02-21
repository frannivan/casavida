package com.casavida.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad de comunicación CRM.
 * Registra mensajes de WhatsApp y Email intercambiados con Leads, Oportunidades o Clientes.
 * Soporta trazabilidad completa del canal de comunicación comercial.
 *
 * @see Lead
 * @since CU03 – Gestión de Leads / CU04 – Gestión de Oportunidades
 */
@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID del Lead u Oportunidad asociado */
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    /** Tipo de canal: WA (WhatsApp) o EMAIL */
    @Column(nullable = false, length = 10)
    private String tipo;

    /** Dirección del mensaje: ENVIADO o RECIBIDO */
    @Column(nullable = false, length = 10)
    private String direccion;

    /** Contenido del mensaje o cuerpo del email */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    /** Nombre del remitente (vendedor, cliente, etc.) */
    @Column(nullable = false)
    private String remitente;

    /** Archivo adjunto (ej. "Cotizacion.pdf"), nullable */
    private String adjunto;

    /** Fecha de envío o recepción */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
    }

    public Mensaje(Long targetId, String tipo, String direccion, String contenido, String remitente) {
        this.targetId = targetId;
        this.tipo = tipo;
        this.direccion = direccion;
        this.contenido = contenido;
        this.remitente = remitente;
        this.fecha = LocalDateTime.now();
    }
}
