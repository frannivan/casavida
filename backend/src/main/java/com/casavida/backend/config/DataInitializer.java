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
        System.out.println("--- STARTING FAIL-SAFE DATA SEEDING (release-1.1) ---");
        
        // 1. Roles seeding
        seedRole(ERole.ROLE_USER);
        seedRole(ERole.ROLE_ADMIN);
        seedRole(ERole.ROLE_VENDEDOR);
        seedRole(ERole.ROLE_RECEPCION);
        seedRole(ERole.ROLE_CONTABILIDAD);
        seedRole(ERole.ROLE_DIRECTIVO);
        seedRole(ERole.ROLE_SOPORTE);

        // 2. Admin User seeding (password: password123)
        if (!userRepository.existsByUsername("admin")) {
            System.out.println("Seeding Admin user...");
            com.casavida.backend.entity.User admin = new com.casavida.backend.entity.User();
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(encoder.encode("password123"));
            
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role ADMIN not found"));
            admin.setRole(adminRole);
            
            userRepository.save(admin);
            System.out.println("Admin user seeded successfully.");
        } else {
            System.out.println("Admin user already exists.");
        }
        System.out.println("--- FAIL-SAFE DATA SEEDING COMPLETED ---");
    }

    private void seedRole(ERole roleName) {
        if (!roleRepository.findByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
            System.out.println("Role " + roleName + " seeded.");
        }
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
