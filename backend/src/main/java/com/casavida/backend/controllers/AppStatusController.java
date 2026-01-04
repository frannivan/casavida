package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppStatusController {

    // Root mapping removed to avoid conflict with potential Zombie RootController
    // @GetMapping("/")
    // public String welcome() { ... }

    @GetMapping("/api/status") // Renamed from 'health' to avoid conflict
    public String status() {
        return "{\"status\":\"UP\", \"source\":\"AppStatusController\"}";
    }

    @GetMapping("/api/lotes/ping")
    public String ping() {
        return "Ping from AppStatusController";
    }
}
