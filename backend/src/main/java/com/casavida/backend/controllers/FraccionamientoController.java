package com.casavida.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.casavida.backend.entity.Fraccionamiento;
import com.casavida.backend.repository.FraccionamientoRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/fraccionamientos")
public class FraccionamientoController {

    @Autowired
    FraccionamientoRepository fraccionamientoRepository;

    @GetMapping("/public")
    public List<Fraccionamiento> getAllFraccionamientos() {
        return fraccionamientoRepository.findAll();
    }
}
