package com.casavida.backend.payload.response;

import java.util.List;

public class ClientDashboardResponse {
    private String clienteNombre;
    private String clienteEmail;
    private List<ContratoSummary> contratos;

    public ClientDashboardResponse(String clienteNombre, String clienteEmail, List<ContratoSummary> contratos) {
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
        this.contratos = contratos;
    }

    // Getters and Setters
    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getClienteEmail() {
        return clienteEmail;
    }

    public void setClienteEmail(String clienteEmail) {
        this.clienteEmail = clienteEmail;
    }

    public List<ContratoSummary> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoSummary> contratos) {
        this.contratos = contratos;
    }

    // Inner DTO for Contract Summary
    public static class ContratoSummary {
        private Long id;
        private String loteNumero;
        private String fraccionamiento;
        private String fechaContrato;
        private String estatus;
        private Double progresoPagos; 
        
        // Detailed metrics
        private java.math.BigDecimal montoTotal;
        private java.math.BigDecimal enganche;
        private java.math.BigDecimal totalPagado;
        private java.math.BigDecimal saldoPendiente;
        private Integer plazo;
        private java.math.BigDecimal cuotaMensual;
        private String manzana;
        private Double area;

        public ContratoSummary(Long id, String loteNumero, String fraccionamiento, String fechaContrato,
                String estatus) {
            this.id = id;
            this.loteNumero = loteNumero;
            this.fraccionamiento = fraccionamiento;
            this.fechaContrato = fechaContrato;
            this.estatus = estatus;
        }

        public Long getId() { return id; }
        public String getLoteNumero() { return loteNumero; }
        public String getFraccionamiento() { return fraccionamiento; }
        public String getFechaContrato() { return fechaContrato; }
        public String getEstatus() { return estatus; }
        public Double getProgresoPagos() { return progresoPagos; }
        public void setProgresoPagos(Double progresoPagos) { this.progresoPagos = progresoPagos; }

        public java.math.BigDecimal getMontoTotal() { return montoTotal; }
        public void setMontoTotal(java.math.BigDecimal montoTotal) { this.montoTotal = montoTotal; }
        public java.math.BigDecimal getEnganche() { return enganche; }
        public void setEnganche(java.math.BigDecimal enganche) { this.enganche = enganche; }
        public java.math.BigDecimal getTotalPagado() { return totalPagado; }
        public void setTotalPagado(java.math.BigDecimal totalPagado) { this.totalPagado = totalPagado; }
        public java.math.BigDecimal getSaldoPendiente() { return saldoPendiente; }
        public void setSaldoPendiente(java.math.BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
        public Integer getPlazo() { return plazo; }
        public void setPlazo(Integer plazo) { this.plazo = plazo; }
        public java.math.BigDecimal getCuotaMensual() { return cuotaMensual; }
        public void setCuotaMensual(java.math.BigDecimal cuotaMensual) { this.cuotaMensual = cuotaMensual; }
        public String getManzana() { return manzana; }
        public void setManzana(String manzana) { this.manzana = manzana; }
        public Double getArea() { return area; }
        public void setArea(Double area) { this.area = area; }
    }
}
