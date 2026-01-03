package com.casavida.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            System.err.println("CRITICAL ERROR: DATABASE_URL is NULL or EMPTY");
            return DataSourceBuilder.create().build();
        }

        // Limpiar espacios, saltos de línea y caracteres invisibles
        String cleanUrl = dbUrl.replaceAll("[\\n\\r\\s\\t]", "").trim();

        System.out.println("DEBUG: URL recibida (longitud " + cleanUrl.length() + ")");

        // Debug de caracteres (para detectar caracteres invisibles)
        StringBuilder hex = new StringBuilder();
        for (char c : cleanUrl.toCharArray()) {
            hex.append(String.format("%02x ", (int) c));
        }
        System.out.println("DEBUG: HEX URL: " + hex.toString());

        // Si ya tiene jdbc: lo respetamos, si no lo agregamos
        if (!cleanUrl.startsWith("jdbc:")) {
            if (cleanUrl.startsWith("postgresql://")) {
                cleanUrl = "jdbc:" + cleanUrl;
            } else {
                // Caso extremo: No tiene ni jdbc ni postgresql:// (solo el host)
                cleanUrl = "jdbc:postgresql://" + cleanUrl;
            }
        }

        // Eliminar duplicados si los hay (ej: jdbc:postgresql://...postgresql://...)
        int secondProto = cleanUrl.indexOf("postgresql://", 15);
        if (secondProto != -1) {
            System.err.println("DEBUG: Detectado duplicado en URL, recortando...");
            cleanUrl = cleanUrl.substring(0, secondProto);
            // Limpiar si quedó un & o ? al final
            if (cleanUrl.endsWith("&") || cleanUrl.endsWith("?")) {
                cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
            }
        }

        System.out.println("DEBUG URL FINAL A USAR: [" + cleanUrl + "]");

        return DataSourceBuilder.create()
                .url(cleanUrl)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }
}
