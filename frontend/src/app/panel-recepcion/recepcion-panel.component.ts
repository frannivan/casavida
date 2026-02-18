import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BoardAdminComponent } from '../board-admin/board-admin.component';

@Component({
  selector: 'app-recepcion-panel',
  standalone: true,
  imports: [CommonModule, FormsModule, BoardAdminComponent],
  template: `
    <div class="container py-3">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-credit-card text-primary me-2"></i>Panel de Recepción</h2>
        <span class="badge bg-info">ROLE_RECEPCION</span>
      </div>
      
      <div class="row mb-4">
        <div class="col-md-4">
          <div class="card bg-primary text-white">
            <div class="card-body">
              <h5>Validación de Pagos</h5>
              <p class="small">Verificar y validar pagos de clientes</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card bg-success text-white">
            <div class="card-body">
              <h5>Registro de Contratos</h5>
              <p class="small">Generar nuevos contratos de venta</p>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card bg-info text-white">
            <div class="card-body">
              <h5>Gestión de Clientes</h5>
              <p class="small">Ver clientes y prospectos</p>
            </div>
          </div>
        </div>
      </div>
      
      <app-board-admin [view]="'payments'"></app-board-admin>
    </div>
  `
})
export class RecepcionPanelComponent implements OnInit {
  ngOnInit(): void {
    console.log('[RecepcionPanel] Inicializado');
  }
}
