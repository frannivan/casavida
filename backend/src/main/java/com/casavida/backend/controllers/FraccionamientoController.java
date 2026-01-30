package com.casavida.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.Fraccionamiento;
import com.casavida.backend.repository.FraccionamientoRepository;

@RestController
@RequestMapping("/api/fraccionamientos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FraccionamientoController {

    public FraccionamientoController() {
        System.out.println("--- FraccionamientoController INSTANTIATED ---");
    }

    @GetMapping("/ping")
    public String ping() {
        return "FraccionamientoController is UP";
    }

    @Autowired
    FraccionamientoRepository fraccionamientoRepository;

    @GetMapping("/public")
    public List<Fraccionamiento> getAllFraccionamientos() {
        return fraccionamientoRepository.findAll();
    }

    @GetMapping("/public/{id}")
    public org.springframework.http.ResponseEntity<?> getFraccionamientoById(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        java.util.Optional<Fraccionamiento> fraccionamiento = fraccionamientoRepository.findById(id);
        if (fraccionamiento.isPresent()) {
            return org.springframework.http.ResponseEntity.ok(fraccionamiento.get());
        } else {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
    }

    @org.springframework.web.bind.annotation.PostMapping("/create")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<?> createFraccionamiento(
            @org.springframework.web.bind.annotation.RequestBody Fraccionamiento fraccionamiento) {
        fraccionamientoRepository.save(fraccionamiento);
        return org.springframework.http.ResponseEntity
                .ok(new com.casavida.backend.payload.response.MessageResponse("Fraccionamiento creado exitosamente."));
    }

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<?> updateFraccionamiento(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody Fraccionamiento details) {
        return fraccionamientoRepository.findById(id).map(fraccionamiento -> {
            fraccionamiento.setNombre(details.getNombre());
            fraccionamiento.setUbicacion(details.getUbicacion());
            fraccionamiento.setDescripcion(details.getDescripcion());
            fraccionamiento.setLogoUrl(details.getLogoUrl());
            fraccionamiento.setCoordenadasGeo(details.getCoordenadasGeo());
            fraccionamientoRepository.save(fraccionamiento);
            return org.springframework.http.ResponseEntity.ok(new com.casavida.backend.payload.response.MessageResponse(
                    "Fraccionamiento actualizado exitosamente."));
        }).orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public org.springframework.http.ResponseEntity<?> deleteFraccionamiento(
            @org.springframework.web.bind.annotation.PathVariable Long id) {
        if (fraccionamientoRepository.existsById(id)) {
            fraccionamientoRepository.deleteById(id);
            return org.springframework.http.ResponseEntity
                    .ok(new com.casavida.backend.payload.response.MessageResponse("Fraccionamiento eliminado."));
        } else {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
    }
}
