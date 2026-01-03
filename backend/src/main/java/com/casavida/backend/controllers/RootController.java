package com.casavida.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public String welcome() {
        return "CasaVida Backend is running! Try /api/health or /swagger-ui/index.html";
    }
}
