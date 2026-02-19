package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String welcome() {
        return "CasaVida Backend is ALIVE at Root! (DEPLOY_ID: 1931-PORT-8080)";
    }

    @GetMapping("/api/health")
    public String health() {
        return "{\"status\":\"UP\", \"source\":\"RootController\"}";
    }

    @GetMapping("/api/lotes/ping")
    public String ping() {
        return "Ping from RootController (lotes mapping)";
    }
}
