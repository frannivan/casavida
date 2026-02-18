import { Routes } from '@angular/router';
import { HomeComponent } from './home/home';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { ProfileComponent } from './profile/profile';
import { BoardAdminComponent } from './board-admin/board-admin.component';
import { BoardClientsComponent } from './board-clients/board-clients.component';
import { BoardUser } from './board-user/board-user';
import { AdminUsersComponent } from './admin-users/admin-users.component';

import { LoteDetail } from './lote-detail/lote-detail';
import { FraccionamientoDetailComponent } from './fraccionamiento-detail/fraccionamiento-detail';
import { CrmLeadsComponent } from './admin/crm/leads/crm-leads.component';
import { CrmOpportunitiesComponent } from './admin/crm/opportunities/crm-opportunities.component';

// Importar paneles específicos por rol
import { RecepcionPanelComponent } from './panel-recepcion/recepcion-panel.component';
import { VendedorPanelComponent } from './panel-vendedor/vendedor-panel.component';

// Componentes placeholder para rutas faltantes
import { Component } from '@angular/core';

@Component({
  selector: 'app-fraccionamientos',
  standalone: true,
  template: `
    <app-board-admin [view]="'fraccionamientos'"></app-board-admin>
  `,
  imports: [BoardAdminComponent]
})
export class FraccionamientosComponent {}

@Component({
  selector: 'app-lotes-inventario',
  standalone: true,
  template: `
    <app-board-admin [view]="'lotes'"></app-board-admin>
  `,
  imports: [BoardAdminComponent]
})
export class LotesInventarioComponent {}

import { ReportesComponent } from './reportes/reportes.component';

import { CargaMasivaComponent } from './carga-masiva/carga-masiva.component';

@Component({
  selector: 'app-documentacion',
  standalone: true,
  template: `<div class="container py-4">
    <h2><i class="fas fa-book me-2"></i>Documentación</h2>
    <p class="text-muted">Manuales y documentación del sistema.</p>
    <div class="alert alert-info">
      <i class="fas fa-info-circle me-2"></i>Esta funcionalidad está en desarrollo.
    </div>
  </div>`
})
export class DocumentacionComponent {}

@Component({
  selector: 'app-contabilidad',
  standalone: true,
  template: `<div class="container py-4">
    <h2><i class="fas fa-file-invoice-dollar me-2"></i>Panel de Contabilidad</h2>
    <p class="text-muted">Gestión de pagos y reportes financieros.</p>
    <hr>
    <app-board-admin [view]="'payments'"></app-board-admin>
  </div>`,
  imports: [BoardAdminComponent]
})
export class ContabilidadComponent {}

@Component({
  selector: 'app-directivo',
  standalone: true,
  template: `<div class="container py-4">
    <h2><i class="fas fa-chart-line me-2"></i>Panel Directivo</h2>
    <p class="text-muted">KPIs e inteligencia de negocio.</p>
    <hr>
    <app-board-admin></app-board-admin>
  </div>`,
  imports: [BoardAdminComponent]
})
export class DirectivoComponent {}

import { CotizadorComponent } from './cotizador/cotizador.component';
import { FraccionamientoEditComponent } from './fraccionamiento-edit/fraccionamiento-edit.component';

@Component({
  selector: 'app-generar-contrato',
  standalone: true,
  template: `<div class="container py-4">
    <h2><i class="fas fa-file-signature me-2"></i>Generar Contrato</h2>
    <p class="text-muted">Creación de nuevos contratos de venta.</p>
    <hr>
    <app-board-admin [view]="'contracts'"></app-board-admin>
  </div>`,
  imports: [BoardAdminComponent]
})
export class GenerarContratoComponent {}

@Component({
  selector: 'app-leads-short',
  standalone: true,
  template: `<div class="container py-4">
    <h2><i class="fas fa-user-tag me-2"></i>Prospectos</h2>
    <p class="text-muted">Gestión de leads y prospectos.</p>
    <hr>
    <app-crm-leads></app-crm-leads>
  </div>`,
  imports: [CrmLeadsComponent]
})
export class LeadsShortComponent {}

export const routes: Routes = [
    // Rutas públicas
    { path: 'home', component: HomeComponent },
    { path: 'lote/:id', component: LoteDetail },
    { path: 'fraccionamiento/:id/edit', component: FraccionamientoEditComponent },
    { path: 'fraccionamiento/:id', component: FraccionamientoDetailComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    { path: 'profile', component: ProfileComponent },
    
    // Rutas de usuario
    { path: 'user', component: BoardUser },
    
    // Rutas de Admin
    { path: 'admin', component: BoardAdminComponent },
    { path: 'admin/clientes', component: BoardClientsComponent },
    { path: 'admin/users', component: AdminUsersComponent },
    { path: 'admin/crm/leads', component: CrmLeadsComponent },
    { path: 'admin/crm/opportunities', component: CrmOpportunitiesComponent },
    
    // Rutas de Inventario
    { path: 'admin/fraccionamientos', component: FraccionamientosComponent },
    { path: 'admin/lotes', component: LotesInventarioComponent },
    
    // Rutas de Herramientas
    { path: 'admin/reportes', component: ReportesComponent },
    { path: 'admin/carga-datos', component: CargaMasivaComponent },
    { path: 'admin/documentacion', component: DocumentacionComponent },
    
    // Rutas adicionales para Recepción y otros roles
    { path: 'admin/generar-contrato', component: GenerarContratoComponent },
    { path: 'admin/leads', component: LeadsShortComponent },
    
    // Rutas para otros roles - Cada uno con su propio panel personalizado
    { path: 'recepcion', component: RecepcionPanelComponent },
    { path: 'panel-vendedor', component: VendedorPanelComponent },
    { path: 'contabilidad', component: ContabilidadComponent },
    { path: 'directivo', component: DirectivoComponent },
    { path: 'cotizar', component: CotizadorComponent },
    
    // Redirect default
    { path: '', redirectTo: 'home', pathMatch: 'full' }
];
