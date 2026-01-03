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

        System.out.println("--- DB CONFIG START ---");
        System.out.println("Input URL: " + dbUrl);

        String cleanUrl = dbUrl.trim();
        String finalJdbcUrl;
        String finalUser = username;
        String finalPass = password;

        try {
            // Manejar formato postgresql://user:pass@host/db
            if (cleanUrl.startsWith("postgresql://") || cleanUrl.startsWith("postgres://")) {
                String sub = cleanUrl.replaceFirst("postgres(ql)?://", "");

                // Extraer user:pass si existen
                if (sub.contains("@")) {
                    String credentials = sub.substring(0, sub.indexOf("@"));
                    String remainder = sub.substring(sub.indexOf("@") + 1);

                    if (credentials.contains(":")) {
                        finalUser = credentials.substring(0, credentials.indexOf(":"));
                        finalPass = credentials.substring(credentials.indexOf(":") + 1);
                    } else {
                        finalUser = credentials;
                    }

                    // Reconstruir la URL de JDBC sin credenciales internas
                    finalJdbcUrl = "jdbc:postgresql://" + remainder;
                } else {
                    finalJdbcUrl = "jdbc:postgresql://" + sub;
                }
            } else if (cleanUrl.startsWith("jdbc:postgresql://")) {
                finalJdbcUrl = cleanUrl;
            } else {
                finalJdbcUrl = "jdbc:postgresql://" + cleanUrl;
            }

            // Quitar parámetros problemáticos del final de la URL si se colaron
            if (finalJdbcUrl.contains("channel_binding=")) {
                finalJdbcUrl = finalJdbcUrl.split("channel_binding=")[0];
                if (finalJdbcUrl.endsWith("&") || finalJdbcUrl.endsWith("?")) {
                    finalJdbcUrl = finalJdbcUrl.substring(0, finalJdbcUrl.length() - 1);
                }
            }

            System.out.println("Final JDBC URL: " + finalJdbcUrl);
            System.out.println("Final User: " + finalUser);

            return DataSourceBuilder.create()
                    .url(finalJdbcUrl)
                    .driverClassName("org.postgresql.Driver")
                    .username(finalUser)
                    .password(finalPass)
                    .build();

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR during DataSource creation: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
