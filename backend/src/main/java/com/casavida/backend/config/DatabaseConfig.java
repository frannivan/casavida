package com.casavida.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${app.datasource.url}")
    private String dbUrl;

    @Value("${app.datasource.username}")
    private String username;

    @Value("${app.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        if (dbUrl == null || dbUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL is not set!");
        }

        // Limpiar URL
        String cleanUrl = dbUrl.trim();
        if (cleanUrl.startsWith("postgresql://")) {
            cleanUrl = "jdbc:" + cleanUrl;
        }

        // Quitar parámetros problemáticos
        if (cleanUrl.contains("channel_binding=")) {
            cleanUrl = cleanUrl.split("channel_binding=")[0];
            if (cleanUrl.endsWith("&") || cleanUrl.endsWith("?")) {
                cleanUrl = cleanUrl.substring(0, cleanUrl.length() - 1);
            }
        }

        System.out.println("--- DB CONFIG START ---");
        System.out.println("Original URL: " + dbUrl);
        System.out.println("Clean URL: " + cleanUrl);

        try {
            // Forzar carga del driver
            Class.forName("org.postgresql.Driver");
            System.out.println("Driver org.postgresql.Driver loaded successfully.");
        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to load PostgreSQL Driver!");
            e.printStackTrace();
        }

        return DataSourceBuilder.create()
                .url(cleanUrl)
                .driverClassName("org.postgresql.Driver")
                .username(username)
                .password(password)
                .build();
    }
}
