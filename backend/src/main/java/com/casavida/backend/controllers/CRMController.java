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
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'RECEPCION')")
    public List<Lead> getAllLeads() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = auth.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());

        return crmService.getAllLeadsFiltered(roles);
    }

    @PostMapping("/leads")
    // Permitido para todos (Web/Chatbot) - se configura en WebSecurityConfig
    public ResponseEntity<Lead> createLead(@RequestBody Lead lead) {
        return ResponseEntity.ok(crmService.createLead(lead));
    }

    @PostMapping("/leads/{id}/convert")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Opportunity> convertLead(@PathVariable Long id, @RequestParam Long loteId) {
        return ResponseEntity.ok(crmService.convertLeadToOpportunity(id, loteId));
    }

    // OPPORTUNITIES
    @GetMapping("/opportunities")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public List<Opportunity> getAllOpportunities() {
        return crmService.getAllOpportunities();
    }

    @PutMapping("/opportunities/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Opportunity> updateOpportunity(@PathVariable Long id, @RequestBody Opportunity opportunity) {
        return ResponseEntity.ok(crmService.updateOpportunity(id, opportunity));
    }

    @PostMapping("/opportunities/{id}/convert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cliente> convertOpportunity(@PathVariable Long id) {
        return ResponseEntity.ok(crmService.convertOpportunityToClient(id));
    }
}
