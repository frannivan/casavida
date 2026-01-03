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
        String finalUrl = dbUrl.trim();

        System.out.println("DEBUG: Configurando DataSource con URL: " + finalUrl);

        // Limpieza de posibles duplicados o errores de pegado
        if (finalUrl.contains("postgresql://")
                && finalUrl.indexOf("postgresql://") != finalUrl.lastIndexOf("postgresql://")) {
            System.err.println("DEBUG ERROR: URL detectada como duplicada. Tomando solo la primera parte.");
            finalUrl = finalUrl.substring(0, finalUrl.indexOf("postgresql://", 10)).trim();
        }

        // Asegurar el prefijo jdbc:
        if (finalUrl.startsWith("postgresql://")) {
            finalUrl = "jdbc:" + finalUrl;
        }

        // Eliminar parámetros que a veces dan problemas en versiones viejas del driver
        if (finalUrl.contains("channel_binding=")) {
            finalUrl = finalUrl.split("channel_binding=")[0];
            if (finalUrl.endsWith("&") || finalUrl.endsWith("?")) {
                finalUrl = finalUrl.substring(0, finalUrl.length() - 1);
            }
        }

        System.out.println("DEBUG URL FINAL: " + finalUrl);

        return DataSourceBuilder.create()
                .url(finalUrl)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }
}
