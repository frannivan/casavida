package com.casavida.backend.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.casavida.backend.payload.response.MessageResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller for handling image uploads and retrieval.
 * Supported formats: JPEG, PNG, PDF (auto-converted to PNG), WebP
 * Maximum file size: 50MB (configured in application.properties)
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final Path root = Paths.get("uploads");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf", "webp");

    public ImageController() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    private boolean isValidImageFormat(String filename) {
        if (filename == null || !filename.contains(".")) {
            return false;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        // Validate file has a name
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        // Validate format
        if (!isValidImageFormat(originalFilename) && !"application/pdf".equals(file.getContentType())) {
            throw new IllegalArgumentException("Formato no soportado. Formatos permitidos: JPEG, PNG, PDF, WebP");
        }

        String filename = UUID.randomUUID().toString();
        String extension = "";

        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Check if PDF
        if ("application/pdf".equals(file.getContentType()) || extension.equalsIgnoreCase(".pdf")) {
            try (org.apache.pdfbox.pdmodel.PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(file.getInputStream())) {
                org.apache.pdfbox.rendering.PDFRenderer pdfRenderer = new org.apache.pdfbox.rendering.PDFRenderer(document);
                // Render first page at 150 DPI (good balance for web)
                java.awt.image.BufferedImage bim = pdfRenderer.renderImageWithDPI(0, 150, org.apache.pdfbox.rendering.ImageType.RGB);

                filename = filename + "_converted.png";
                javax.imageio.ImageIO.write(bim, "png", this.root.resolve(filename).toFile());

                String fileUrl = "/casavida/api/images/" + filename;
                return ResponseEntity.ok(new MessageResponse(fileUrl));
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar PDF: " + e.getMessage(), e);
            }
        } else {
            // Regular Image Upload
            filename = filename + "_" + originalFilename;
            try {
                Files.copy(file.getInputStream(), this.root.resolve(filename));
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar archivo: " + e.getMessage(), e);
            }
            String fileUrl = "/casavida/api/images/" + filename;
            return ResponseEntity.ok(new MessageResponse(fileUrl));
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Path file = root.resolve(filename);
        try {
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer archivo: " + e.getMessage(), e);
        }
    }
}
