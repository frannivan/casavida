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
        try {
            // 1. Roles
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                roleRepository.save(new Role(null, ERole.ROLE_USER));
            }

            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                roleRepository.save(new Role(null, ERole.ROLE_ADMIN));
            }

            // 2. Admin User
            if (!userRepository.existsByUsername("admin")) {
                com.casavida.backend.entity.User user = new com.casavida.backend.entity.User();
                user.setUsername("admin");
                user.setEmail("admin@casavida.com");
                user.setPassword(encoder.encode("password"));

                java.util.Set<Role> roles = new java.util.HashSet<>();
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role no encontrado."));
                roles.add(adminRole);
                user.setRoles(roles);

                userRepository.save(user);
                System.out.println("User 'admin' created with password 'password'");
            }

            // 3. Fraccionamiento Demo
            com.casavida.backend.entity.Fraccionamiento residencial = fraccionamientoRepository
                    .findByNombre("Residencial Las Palmas").orElse(null);
            if (residencial == null) {
                residencial = new com.casavida.backend.entity.Fraccionamiento(
                        "Residencial Las Palmas", "Cancún, Quintana Roo",
                        "Exclusivo fraccionamiento con seguridad 24/7 y acceso a playa.",
                        "/images/logos/palmas.png", "21.1619, -86.8515");
                fraccionamientoRepository.save(residencial);
                System.out.println("Created Fraccionamiento: Las Palmas");
            } else {
                // Ensure existing has coords (migration)
                if (residencial.getCoordenadasGeo() == null) {
                    residencial.setCoordenadasGeo("21.1619, -86.8515");
                    fraccionamientoRepository.save(residencial);
                }
            }

            com.casavida.backend.entity.Fraccionamiento sieteMares = fraccionamientoRepository
                    .findByNombre("7 Mares Residencial").orElse(null);
            if (sieteMares == null) {
                sieteMares = new com.casavida.backend.entity.Fraccionamiento(
                        "7 Mares Residencial", "Mazunte, Oaxaca",
                        "Desarrollo eco-turístico con vista al mar.",
                        "/images/logos/7mares.png", "15.6665, -96.5556");
                fraccionamientoRepository.save(sieteMares);
                System.out.println("Created Fraccionamiento: 7 Mares");
            } else {
                if (sieteMares.getCoordenadasGeo() == null) {
                    sieteMares.setCoordenadasGeo("15.6665, -96.5556");
                    fraccionamientoRepository.save(sieteMares);
                }
            }

            // 4. Demo Lotes
            if (loteRepository.count() == 0) {
                createLote("A001", "Manzana A", 200.00, 150000.00, "/images/lotes/lote-a001.svg", "21.1619, -86.8515",
                        residencial);
                createLote("A002", "Manzana A", 250.00, 180000.00, "/images/lotes/lote-a002.svg", "21.1630, -86.8520",
                        residencial);
                createLote("B001", "Manzana B", 300.00, 220000.00, "/images/lotes/lote-b001.svg", "21.1640, -86.8530",
                        residencial);
                createLote("B002", "Manzana B", 300.00, 220000.00, "/images/lotes/lote-b002.svg", "21.1650, -86.8540",
                        residencial);

                // Un lote vendido
                com.casavida.backend.entity.Lote loteVendido = new com.casavida.backend.entity.Lote();
                loteVendido.setNumeroLote("C001");
                loteVendido.setManzana("Manzana C");
                loteVendido.setAreaMetrosCuadrados(500.00);
                loteVendido.setPrecioTotal(java.math.BigDecimal.valueOf(400000.00));
                loteVendido.setEstatus(com.casavida.backend.entity.EStatusLote.VENDIDO);
                loteVendido.setImagenUrl("/images/lotes/lote-c001.svg");
                loteVendido.setFraccionamiento(residencial);
                loteVendido.getGaleriaImagenes().add("/images/lotes/lote-c001.svg"); // Add to gallery
                loteRepository.save(loteVendido);

                loteRepository.save(loteVendido);

                System.out.println("Dummy data for Lotes created.");
            }

            // CORRECT APPROACH for this script:
            boolean m001Exists = false;
            java.util.List<com.casavida.backend.entity.Lote> checkLotes = loteRepository.findAll();
            for (com.casavida.backend.entity.Lote l : checkLotes) {
                if ("M001".equals(l.getNumeroLote())) {
                    m001Exists = true;
                    break;
                }
            }

            if (!m001Exists && sieteMares != null) {
                createLote("M001", "Manzana Mar", 1000.00, 550000.00, "/images/lotes/lote-a001.svg",
                        "15.6665, -96.5556", sieteMares);
                System.out.println("Created missing M001 lot for 7 Mares");
            }

            // 5. MIGRATION: Fix old placeholder URLs if they exist AND assign
            // Fraccionamiento
            java.util.List<com.casavida.backend.entity.Lote> allLotes = loteRepository.findAll();
            for (com.casavida.backend.entity.Lote lote : allLotes) {
                try {
                    boolean changed = false;
                    // Fix URL
                    if (lote.getImagenUrl() != null && lote.getImagenUrl().contains("via.placeholder.com")) {
                        String newUrl = "https://placehold.co/600x400?text=Lote+" + lote.getNumeroLote();
                        lote.setImagenUrl(newUrl);
                        changed = true;
                    }
                    // Assign Fraccionamiento if null
                    if (lote.getFraccionamiento() == null && residencial != null) {
                        lote.setFraccionamiento(residencial);
                        changed = true;
                    }
                    // Init Gallery if empty
                    if (lote.getGaleriaImagenes().isEmpty() && lote.getImagenUrl() != null) {
                        lote.getGaleriaImagenes().add(lote.getImagenUrl());
                        // Add different placeholder images for gallery testing
                        lote.getGaleriaImagenes().add("https://placehold.co/600x400/2ecc71/FFF?text=Vista+Panoramica");
                        lote.getGaleriaImagenes().add("https://placehold.co/600x400/3498db/FFF?text=Amenidades");
                        changed = true;
                    }

                    // MIGRATION: Backfill Coordinates if missing
                    if (lote.getCoordenadasGeo() == null) {
                        // Assign random variation around Cancun/Mexico for demo purposes
                        double baseLat = 21.16 + (Math.random() * 0.01);
                        double baseLng = -86.85 + (Math.random() * 0.01);
                        // If it's the specific "7 Mares" lot, put it in Mazunte
                        if (lote.getNumeroLote().equals("M001")) {
                            lote.setCoordenadasGeo("15.6665, -96.5556");
                        } else {
                            lote.setCoordenadasGeo(String.format(java.util.Locale.US, "%.5f, %.5f", baseLat, baseLng));
                        }
                        changed = true;
                    }

                    if (changed) {
                        loteRepository.save(lote);
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating lote " + lote.getId() + ": " + e.getMessage());
                    // Continue to next lote
                }
            }
            // 6. Demo Client User and Contract
            if (!userRepository.existsByUsername("client")) {
                // User
                com.casavida.backend.entity.User clientUser = new com.casavida.backend.entity.User();
                clientUser.setUsername("client");
                clientUser.setEmail("client@casavida.com");
                clientUser.setPassword(encoder.encode("password"));

                java.util.Set<Role> roles = new java.util.HashSet<>();
                Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElse(null);
                if (userRole != null)
                    roles.add(userRole);
                clientUser.setRoles(roles);
                userRepository.save(clientUser);
                System.out.println("User 'client' created.");

                // Cliente Profile
                com.casavida.backend.entity.Cliente cliente = new com.casavida.backend.entity.Cliente();
                cliente.setNombre("Juan");
                cliente.setApellidos("Perez Cliente");
                cliente.setEmail("client@casavida.com"); // Matches User Email
                cliente.setTelefono("555-000-1111");
                clienteRepo.save(cliente);

                // Contrato (Linked to Lote A001 if available, or create dummy)
                java.util.List<com.casavida.backend.entity.Lote> lotes = loteRepository.findAll();
                if (!lotes.isEmpty()) {
                    com.casavida.backend.entity.Lote lote = lotes.get(0);
                    // Mark as SOLD if allowed, or just link contract
                    com.casavida.backend.entity.Contrato contrato = new com.casavida.backend.entity.Contrato();
                    contrato.setCliente(cliente);
                    contrato.setLote(lote);
                    contrato.setFechaContrato(java.time.LocalDate.now().minusMonths(3));
                    contrato.setMontoTotal(lote.getPrecioTotal());
                    contrato.setEstatus(com.casavida.backend.entity.EStatusContrato.ACTIVO);
                    contratoRepo.save(contrato);
                    System.out.println("Demo Contract created for 'client'");

                    // 7. Demo Pagos (NEW)
                    if (pagoRepository.count() == 0) {
                        java.math.BigDecimal mensualidad = contrato.getMontoTotal()
                                .divide(java.math.BigDecimal.valueOf(12), java.math.RoundingMode.HALF_UP);

                        // Pago 1
                        com.casavida.backend.entity.Pago pago1 = new com.casavida.backend.entity.Pago();
                        pago1.setContrato(contrato);
                        pago1.setFechaPago(java.time.LocalDate.now().minusMonths(2));
                        pago1.setMonto(mensualidad);
                        pago1.setReferencia("SPEI-001");
                        pago1.setConcepto("Mensualidad 1");
                        pagoRepository.save(pago1);

                        // Pago 2
                        com.casavida.backend.entity.Pago pago2 = new com.casavida.backend.entity.Pago();
                        pago2.setContrato(contrato);
                        pago2.setFechaPago(java.time.LocalDate.now().minusMonths(1));
                        pago2.setMonto(mensualidad);
                        pago2.setReferencia("EFECTIVO-002");
                        pago2.setConcepto("Mensualidad 2");
                        pagoRepository.save(pago2);

                        System.out.println("Dummy data for Pagos created.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CRITICAL: DataInitializer failed: " + e.getMessage());
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
