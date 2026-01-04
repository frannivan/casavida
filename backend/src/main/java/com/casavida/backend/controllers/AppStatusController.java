package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppStatusController {

    @GetMapping("/")
    public String welcome() {
        return "CasaVida Backend is ALIVE on Render/Railway! (Status: OK)";
    }

    @GetMapping("/api/status") // Renamed from 'health' to avoid conflict
    public String status() {
        return "{\"status\":\"UP\", \"source\":\"AppStatusController\"}";
    }

    @GetMapping("/api/lotes/ping")
    public String ping() {
        return "Ping from AppStatusController";
    }
}
