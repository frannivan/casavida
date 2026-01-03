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
        String finalUrl = dbUrl;

        // Si la URL viene de una variable de entorno tipo postgresql://
        // pero le falta el prefijo jdbc: exigido por el driver de Java
        if (finalUrl.startsWith("postgresql://")) {
            finalUrl = "jdbc:" + finalUrl;
        }

        return DataSourceBuilder.create()
                .url(finalUrl)
                .driverClassName(driverClassName)
                .username(username)
                .password(password)
                .build();
    }
}
