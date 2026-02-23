import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CRMService } from '../../../services/crm.service';
import { LoteService } from '../../../services/lote';
import { CommunicationModalComponent } from '../communication-modal.component';

@Component({
    selector: 'app-crm-leads',
    templateUrl: './crm-leads.component.html',
    styleUrls: ['./crm-leads.component.css'],
    standalone: true,
    imports: [CommonModule, FormsModule, CommunicationModalComponent]
})
export class CrmLeadsComponent implements OnInit {
    leads: any[] = [];
    availableLotes: any[] = [];
    selectedLead: any = null;
    selectedLoteId: number | null = null;
    showConvertModal = false;
    successMsg = '';
    errorMsg = '';
    searchTerm = '';
    showCommModal = false;

    get filteredLeads() {
        if (!this.searchTerm) return this.leads;
        const s = this.searchTerm.toLowerCase();
        return this.leads.filter(l => 
            (l.nombre?.toLowerCase().includes(s) || false) || 
            (l.email?.toLowerCase().includes(s) || false) ||
            (l.telefono?.includes(s) || false)
        );
    }

    private crmService = inject(CRMService);
    private loteService = inject(LoteService);

    constructor() { }

    ngOnInit(): void {
        this.loadLeads();
        this.loadLotes();
    }

    loadLeads(): void {
        this.crmService.getAllLeads().subscribe({
            next: (data: any) => this.leads = data,
            error: (err: any) => this.errorMsg = 'Error al cargar prospectos'
        });
    }

    loadLotes(): void {
        this.loteService.getAllLotes().subscribe({
            next: (data: any) => this.availableLotes = data,
            error: (err: any) => console.error('Error al cargar lotes', err)
        });
    }

    openConvertModal(lead: any): void {
        this.selectedLead = lead;
        this.showConvertModal = true;
        this.errorMsg = '';
        this.successMsg = '';
    }

    openCommModal(lead: any): void {
        this.selectedLead = lead;
        this.showCommModal = true;
    }

    confirmConvert(): void {
        if (!this.selectedLoteId) {
            this.errorMsg = 'Debe seleccionar un lote';
            return;
        }

        this.crmService.convertLeadToOpportunity(this.selectedLead.id, this.selectedLoteId).subscribe({
            next: () => {
                this.successMsg = '¡Convertido a oportunidad exitosamente!';
                this.loadLeads();
                setTimeout(() => this.showConvertModal = false, 2000);
            },
            error: (err: any) => this.errorMsg = 'Error al convertir: ' + (err.message || 'Error desconocido')
        });
    }
}
