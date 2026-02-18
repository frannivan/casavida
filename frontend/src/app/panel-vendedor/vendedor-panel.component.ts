import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BoardAdminComponent } from '../board-admin/board-admin.component';

@Component({
  selector: 'app-vendedor-panel',
  standalone: true,
  imports: [CommonModule, BoardAdminComponent],
  template: `
    <div class="container py-3">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fas fa-briefcase text-success me-2"></i>Panel de Vendedor</h2>
        <span class="badge bg-success">ROLE_VENDEDOR</span>
      </div>
      
      <div class="row mb-4">
        <div class="col-md-3">
          <div class="card bg-success text-white">
            <div class="card-body text-center">
              <i class="fas fa-users fa-2x mb-2"></i>
              <h6>Mis Clientes</h6>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-primary text-white">
            <div class="card-body text-center">
              <i class="fas fa-user-plus fa-2x mb-2"></i>
              <h6>Prospectos</h6>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-info text-white">
            <div class="card-body text-center">
              <i class="fas fa-chart-line fa-2x mb-2"></i>
              <h6>Oportunidades</h6>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card bg-warning text-dark">
            <div class="card-body text-center">
              <i class="fas fa-calculator fa-2x mb-2"></i>
              <h6>Cotizaciones</h6>
            </div>
          </div>
        </div>
      </div>
      
      <app-board-admin></app-board-admin>
    </div>
  `
})
export class VendedorPanelComponent implements OnInit {
  ngOnInit(): void {
    console.log('[VendedorPanel] Inicializado');
  }
}
