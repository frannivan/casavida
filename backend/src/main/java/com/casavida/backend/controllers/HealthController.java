package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    public HealthController() {
        System.out.println("--- HealthController INSTANTIATED ---");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("message", "CasaVida API is running");
        return status;
    }

    @GetMapping("/")
    public String root() {
        return "CasaVida Backend is ALIVE at /api/";
    }
}
