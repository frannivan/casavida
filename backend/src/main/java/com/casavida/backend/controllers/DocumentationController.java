package com.casavida.backend.controllers;

import com.casavida.backend.entity.Contrato;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/docs")
public class DocumentationController {

    private final List<String> KNOWN_DOCS = List.of(
        "ECU_EIU_Editor_Poligonos.md",
        "ECU_EIU_Recepcion_Pagos.md"
    );

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('VENDEDOR')")
    public List<String> listDocs() {
        return KNOWN_DOCS;
    }

    @GetMapping("/{filename:.+}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPCION') or hasRole('VENDEDOR')")
    public ResponseEntity<Resource> getDoc(@PathVariable String filename) {
        try {
            // Validate filename against known list for security
            if (!KNOWN_DOCS.contains(filename)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new org.springframework.core.io.ClassPathResource("docs/" + filename);

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_MARKDOWN)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
