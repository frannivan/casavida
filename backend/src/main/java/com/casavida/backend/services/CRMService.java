package com.casavida.backend.services;

import com.casavida.backend.entity.*;
import com.casavida.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CRMService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Leads logic
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public Lead createLead(Lead lead) {
        if (lead.getFechaRegistro() == null) {
            lead.setFechaRegistro(LocalDateTime.now());
        }
        if (lead.getStatus() == null) {
            lead.setStatus(ELeadStatus.NEW);
        }
        System.out.println("DEBUG: Creating Lead -> " + lead.getNombre() + " (" + lead.getSource() + ")");
        return leadRepository.save(lead);
    }

    @Transactional
    public Opportunity convertLeadToOpportunity(Long leadId, Long loteId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead no encontrado"));
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));

        lead.setStatus(ELeadStatus.QUALIFIED);
        leadRepository.save(lead);

        Opportunity opp = new Opportunity();
        opp.setLead(lead);
        opp.setLote(lote);
        opp.setMontoEstimado(lote.getPrecioTotal());
        opp.setStatus(EOpportunityStatus.NEGOTIATION);

        return opportunityRepository.save(opp);
    }

    // Opportunity logic
    public List<Opportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    public Opportunity updateOpportunity(Long id, Opportunity oppData) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada"));

        opp.setStatus(oppData.getStatus());
        opp.setNotas(oppData.getNotas());
        opp.setMontoEstimado(oppData.getMontoEstimado());
        opp.setFechaCierreEstimada(oppData.getFechaCierreEstimada());

        return opportunityRepository.save(opp);
    }

    @Transactional
    public Cliente convertOpportunityToClient(Long oppId) {
        Opportunity opp = opportunityRepository.findById(oppId)
                .orElseThrow(() -> new RuntimeException("Oportunidad no encontrada"));

        Lead lead = opp.getLead();

        // Create actual Cliente
        Cliente cliente = new Cliente();
        cliente.setNombre(lead.getNombre());
        cliente.setEmail(lead.getEmail());
        cliente.setTelefono(lead.getTelefono());
        // Assume default values or split name if possible
        cliente.setDireccion("Pendiente de actualizar");

        Cliente savedCliente = clienteRepository.save(cliente);

        opp.setStatus(EOpportunityStatus.WON);
        opportunityRepository.save(opp);

        return savedCliente;
    }
}
