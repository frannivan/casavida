package com.casavida.backend.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.EStatusLote;
import com.casavida.backend.entity.Lote;
import com.casavida.backend.payload.response.MessageResponse;
import com.casavida.backend.repository.LoteRepository;

/**
 * CONTROLADOR DE INVENTARIO DE LOTES (CU02)
 * <p>
 * Gestiona la disponibilidad, precios y estatus de las unidades individuales (Lotes).
 * Incluye funcionalidades de filtrado por fraccionamiento y actualización masiva
 * de estatus operativos (Disponible, Apartado, Vendido).
 * 
 * @author CasaVida Systems
 * @version 1.0
 * @see <a href="SRS_CasaVida_ERP.md#cu02-gestión-de-inventario-y-disponibilidad-de-lotes">CU02: Gestión de Lotes</a>
 */
@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    public LoteController() {
        System.out.println("--- LoteController INSTANTIATED ---");
    }

    @GetMapping("/ping")
    public String ping() {
        return "LoteController is UP";
    }

    @Autowired
    LoteRepository loteRepository;

    @GetMapping("/public")
    public List<Lote> getAvailableLotes(
            @RequestParam(required = false) Long fraccionamientoId,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(defaultValue = "asc") String sortDir) {
        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc")
                ? org.springframework.data.domain.Sort.by("precioTotal").descending()
                : org.springframework.data.domain.Sort.by("precioTotal").ascending();

        return loteRepository.searchLotes(java.util.Arrays.asList(EStatusLote.DISPONIBLE, EStatusLote.APARTADO), fraccionamientoId, ubicacion, sort);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getLoteById(@PathVariable Long id) {
        Optional<Lote> lote = loteRepository.findById(id);
        if (lote.isPresent()) {
            return ResponseEntity.ok(lote.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public List<Lote> getAllLotes() {
        return loteRepository.findAll();
    }

    @GetMapping("/adm/by-fraccionamiento/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public List<Lote> getLotesByFraccionamiento(@PathVariable Long id) {
        return loteRepository.findByFraccionamientoId(id);
    }

    @GetMapping("/public/by-fraccionamiento/{id}")
    public List<Lote> getPublicLotesByFraccionamiento(@PathVariable Long id) {
        return loteRepository.findByFraccionamientoId(id);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createLote(@RequestBody Lote lote) {
        loteRepository.save(lote);
        return ResponseEntity.ok(new MessageResponse("Lote creado exitosamente."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLote(@PathVariable Long id, @RequestBody Lote loteDetails) {
        return loteRepository.findById(id).map(lote -> {
            lote.setNumeroLote(loteDetails.getNumeroLote());
            lote.setManzana(loteDetails.getManzana());
            lote.setPrecioTotal(loteDetails.getPrecioTotal());
            lote.setAreaMetrosCuadrados(loteDetails.getAreaMetrosCuadrados());
            lote.setCoordenadasGeo(loteDetails.getCoordenadasGeo());
            lote.setPlanoCoordinates(loteDetails.getPlanoCoordinates());
            lote.setEstatus(loteDetails.getEstatus());
            lote.setDescripcion(loteDetails.getDescripcion());
            lote.setImagenUrl(loteDetails.getImagenUrl());
            if (loteDetails.getGaleriaImagenes() != null) {
                lote.getGaleriaImagenes().clear();
                lote.getGaleriaImagenes().addAll(loteDetails.getGaleriaImagenes());
            }
            if (loteDetails.getFraccionamiento() != null) {
                lote.setFraccionamiento(loteDetails.getFraccionamiento());
            } else {
                lote.setFraccionamiento(null);
            }
            loteRepository.save(lote);
            return ResponseEntity.ok(new MessageResponse("Lote actualizado exitosamente."));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLote(@PathVariable Long id) {
        if (loteRepository.existsById(id)) {
            loteRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Lote eliminado."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/adm/{id}/poligono")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLotePoligono(
            @PathVariable Long id,
            @RequestBody String coordinates) {
        return loteRepository.findById(id).map(lote -> {
            lote.setPlanoCoordinates(coordinates);
            loteRepository.save(lote);
            return ResponseEntity.ok(new MessageResponse("Polígono de lote actualizado"));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/adm/{id}/estatus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateLoteEstatus(
            @PathVariable Long id,
            @RequestBody java.util.Map<String, String> body) {
        String estatus = body.get("estatus");
        return loteRepository.findById(id).map(lote -> {
            lote.setEstatus(EStatusLote.valueOf(estatus));
            loteRepository.save(lote);
            return ResponseEntity.ok(lote);
        }).orElse(ResponseEntity.notFound().build());
    }
}
