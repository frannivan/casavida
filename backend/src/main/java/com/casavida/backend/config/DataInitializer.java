package com.casavida.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.casavida.backend.entity.ERole;
import com.casavida.backend.entity.Role;
import com.casavida.backend.repository.RoleRepository;

@Component
@org.springframework.transaction.annotation.Transactional
public class DataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    com.casavida.backend.repository.UserRepository userRepository;

    @Autowired
    com.casavida.backend.repository.LoteRepository loteRepository;

    @Autowired
    org.springframework.security.crypto.password.PasswordEncoder encoder;

    @Autowired
    com.casavida.backend.repository.FraccionamientoRepository fraccionamientoRepository;

    @Autowired
    com.casavida.backend.repository.ClienteRepository clienteRepo;

    @Autowired
    com.casavida.backend.repository.ContratoRepository contratoRepo;

    @Autowired
    com.casavida.backend.repository.PagoRepository pagoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Data initialization has been migrated to data.sql
        // This method is intentionally left empty to prevent duplicate data insertion.
    }

    private void createLote(String numero, String manzana, Double area, Double precio, String img, String coords,
            com.casavida.backend.entity.Fraccionamiento fraccionamiento) {
        com.casavida.backend.entity.Lote lote = new com.casavida.backend.entity.Lote();
        lote.setNumeroLote(numero);
        lote.setManzana(manzana);
        lote.setAreaMetrosCuadrados(area);
        lote.setPrecioTotal(java.math.BigDecimal.valueOf(precio));
        lote.setCoordenadasGeo(coords);
        lote.setEstatus(com.casavida.backend.entity.EStatusLote.DISPONIBLE);
        lote.setImagenUrl(img);
        lote.setDescripcion(
                "Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.");
        lote.setFraccionamiento(fraccionamiento);
        // Add implicit gallery
        lote.getGaleriaImagenes().add(img);
        lote.getGaleriaImagenes().add("https://placehold.co/600x400/2ecc71/FFF?text=Area+Verde");
        lote.getGaleriaImagenes().add("https://placehold.co/600x400/e74c3c/FFF?text=Acceso+Principal");
        loteRepository.save(lote);
    }
}
