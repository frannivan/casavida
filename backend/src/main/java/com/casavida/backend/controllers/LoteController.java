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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/lotes")
public class LoteController {

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

        return loteRepository.searchLotes(EStatusLote.DISPONIBLE, fraccionamientoId, ubicacion, sort);
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
    @PreAuthorize("hasRole('ADMIN')")
    public List<Lote> getAllLotes() {
        return loteRepository.findAll();
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
            lote.setEstatus(loteDetails.getEstatus());
            lote.setDescripcion(loteDetails.getDescripcion());
            lote.setImagenUrl(loteDetails.getImagenUrl());
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
}
