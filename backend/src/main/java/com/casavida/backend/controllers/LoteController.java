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

@RestController
@RequestMapping("/api/lotes")
/**
 * Controller for managing Lots (Lotes) in the inventory.
 * Provides endpoints for creating, updating, searching, and managing lot details including polygons and status.
 * 
 * <p>Enforces RBAC for inventory management (Admin/Directivo edit, others view).
 * 
 * @author CasaVida Team
 * @since 1.0
 */
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

    /**
     * Public endpoint to search available lots.
     * Used by public map or listing.
     * 
     * @param fraccionamientoId Filter by Fraccionamiento ID (optional)
     * @param ubicacion Filter by location string (optional)
     * @param sortDir Sort direction ("asc" or "desc" by price)
     * @return List of available (or reserved/apartado) {@link Lote} entities
     */
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

    /**
     * Retrieves specific lot details (Public).
     * 
     * @param id The ID of the Lote
     * @return ResponseEntity with {@link Lote} or 404 if not found
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<?> getLoteById(@PathVariable Long id) {
        Optional<Lote> lote = loteRepository.findById(id);
        if (lote.isPresent()) {
            return ResponseEntity.ok(lote.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves all lots in the system (Internal).
     * 
     * @return List of all {@link Lote} entities
     */
    @GetMapping("/all")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.VIEW')")
    public List<Lote> getAllLotes() {
        return loteRepository.findAll();
    }

    /**
     * Retrieves all lots belonging to a specific Fraccionamiento.
     * 
     * @param id The ID of the Fraccionamiento
     * @return List of {@link Lote} entities
     */
    @GetMapping("/adm/by-fraccionamiento/{id}")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.VIEW')")
    public List<Lote> getLotesByFraccionamiento(@PathVariable Long id) {
        return loteRepository.findByFraccionamientoId(id);
    }

    /**
     * Creates a new Lote in the inventory.
     * 
     * @param lote The {@link Lote} entity to create
     * @return ResponseEntity with success message
     */
    @PostMapping("/create")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.CREATE')")
    public ResponseEntity<?> createLote(@RequestBody Lote lote) {
        loteRepository.save(lote);
        return ResponseEntity.ok(new MessageResponse("Lote creado exitosamente."));
    }

    /**
     * Updates an existing Lote's details.
     * 
     * @param id The ID of the Lote
     * @param loteDetails {@link Lote} object with updated fields
     * @return ResponseEntity with success message or 404 if not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.EDIT')")
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

    /**
     * Deletes a Lote from the inventory.
     * 
     * @param id The ID of the Lote
     * @return ResponseEntity with success message or 404 if not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.DELETE')")
    public ResponseEntity<?> deleteLote(@PathVariable Long id) {
        if (loteRepository.existsById(id)) {
            loteRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Lote eliminado."));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates only the polygon coordinates of a Lote.
     * Useful for map editors.
     * 
     * @param id The ID of the Lote
     * @param coordinates The new coordinates string
     * @return ResponseEntity with success message or 404 if not found
     */
    @PutMapping("/adm/{id}/poligono")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.EDIT')")
    public ResponseEntity<?> updateLotePoligono(
            @PathVariable Long id,
            @RequestBody String coordinates) {
        return loteRepository.findById(id).map(lote -> {
            lote.setPlanoCoordinates(coordinates);
            loteRepository.save(lote);
            return ResponseEntity.ok(new MessageResponse("Polígono de lote actualizado"));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates only the status of a Lote.
     * 
     * @param id The ID of the Lote
     * @param body Map containing the new 'estatus'
     * @return ResponseEntity with updated Lote or 404 if not found
     */
    @PutMapping("/adm/{id}/estatus")
    @PreAuthorize("hasPermission(null, 'INVENTARIO.EDIT')")
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
