import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-crm-lead-dossier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './crm-lead-dossier.component.html',
  styleUrls: ['./crm-lead-dossier.component.css']
})
export class CrmLeadDossierComponent {
  @Input() lead: any;
  @Output() onClose = new EventEmitter<void>();
  @Output() onAction = new EventEmitter<{type: string, payload?: any}>();

  statuses = [
    { key: 'NEW', label: 'Nuevo' },
    { key: 'CONTACTED', label: 'Contactado' },
    { key: 'PRICE_LIST_SENT', label: 'Lista de Precios' },
    { key: 'BUDGET_SENT', label: 'Presupuesto' },
    { key: 'QUALIFIED', label: 'Calificado' }
  ];

  getStatusIndex(currentStatus: string): number {
    return this.statuses.findIndex(s => s.key === currentStatus);
  }

  isCompleted(statusKey: string): boolean {
    const currentIndex = this.getStatusIndex(this.lead?.status);
    const statusIndex = this.statuses.findIndex(s => s.key === statusKey);
    return statusIndex < currentIndex;
  }

  isActive(statusKey: string): boolean {
    return this.lead?.status === statusKey;
  }

  emitAction(type: string, payload?: any) {
    this.onAction.emit({ type, payload });
  }

  close() {
    this.onClose.emit();
  }
}
