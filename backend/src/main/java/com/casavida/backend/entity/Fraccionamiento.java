package com.casavida.backend.entity;

import javax.persistence.*;

@Entity
@Table(name = "fraccionamientos")
public class Fraccionamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String ubicacion;
    private String descripcion;
    private String logoUrl;
    private String coordenadasGeo;

    public Fraccionamiento() {
    }

    public Fraccionamiento(String nombre, String ubicacion, String descripcion, String logoUrl, String coordenadasGeo) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.logoUrl = logoUrl;
        this.coordenadasGeo = coordenadasGeo;
    }

    public String getCoordenadasGeo() {
        return coordenadasGeo;
    }

    public void setCoordenadasGeo(String coordenadasGeo) {
        this.coordenadasGeo = coordenadasGeo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
