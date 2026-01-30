package com.casavida.backend.controllers;

import com.casavida.backend.entity.Lead;
import com.casavida.backend.entity.Opportunity;
import com.casavida.backend.entity.Cliente;
import com.casavida.backend.services.CRMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm")
public class CRMController {

    @Autowired
    private CRMService crmService;

    // LEADS
    @GetMapping("/leads")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Lead> getAllLeads() {
        return crmService.getAllLeads();
    }

    @PostMapping("/leads")
    // Permitido para todos (Web/Chatbot) - se configura en WebSecurityConfig
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        return ResponseEntity.ok(crmService.createLead(lead));
    }

    @PostMapping("/leads/{id}/convert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Opportunity> convertLead(@PathVariable Long id, @RequestParam Long loteId) {
        return ResponseEntity.ok(crmService.convertLeadToOpportunity(id, loteId));
    }

    // OPPORTUNITIES
    @GetMapping("/opportunities")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Opportunity> getAllOpportunities() {
        return crmService.getAllOpportunities();
    }

    @PutMapping("/opportunities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Opportunity> updateOpportunity(@PathVariable Long id, @RequestBody Opportunity opportunity) {
        return ResponseEntity.ok(crmService.updateOpportunity(id, opportunity));
    }

    @PostMapping("/opportunities/{id}/convert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cliente> convertOpportunity(@PathVariable Long id) {
        return ResponseEntity.ok(crmService.convertOpportunityToClient(id));
    }
}
