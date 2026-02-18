import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ReporteService, UploadService } from '../services/reporte';
import { ClienteService } from '../services/cliente';
import { FraccionamientoService } from '../services/fraccionamiento';
import { AdminService } from '../services/admin';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0"><i class="fas fa-chart-bar me-2"></i>Reportes</h4>
        </div>
        <div class="card-body">
          <!-- Selección de tipo de reporte -->
          <div class="row mb-4">
            <div class="col-md-6">
              <label class="form-label"><strong>Tipo de Reporte:</strong></label>
              <select class="form-control" [(ngModel)]="tipoReporte" (change)="onTipoReporteChange()">
                <option value="">-- Seleccionar reporte --</option>
                <option value="lotes">🏠 Reporte de Lotes</option>
                <option value="clientes">👥 Reporte de Clientes</option>
                <option value="contratos">📝 Reporte de Contratos</option>
                <option value="pagos">💳 Reporte de Pagos</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label"><strong>Formato de Exportación:</strong></label>
              <div class="btn-group w-100">
                <button class="btn" [class.btn-primary]="formato === 'pantalla'" [class.btn-outline-primary]="formato !== 'pantalla'" (click)="setFormato('pantalla')">
                  <i class="fas fa-desktop me-2"></i>Pantalla
                </button>
                <button class="btn" [class.btn-success]="formato === 'excel'" [class.btn-outline-success]="formato !== 'excel'" (click)="setFormato('excel')">
                  <i class="fas fa-file-excel me-2"></i>Excel
                </button>
                <button class="btn" [class.btn-danger]="formato === 'pdf'" [class.btn-outline-danger]="formato !== 'pdf'" (click)="setFormato('pdf')">
                  <i class="fas fa-file-pdf me-2"></i>PDF
                </button>
              </div>
            </div>
          </div>

          <!-- Filtros dinámicos -->
          <div *ngIf="tipoReporte" class="card bg-light mb-4">
            <div class="card-body">
              <h5><i class="fas fa-filter me-2"></i>Filtros</h5>

              <div class="row">
                <!-- Filtro por fecha - disponible para todos -->
                <div class="col-md-4">
                  <div class="form-group">
                    <label>Fecha Desde:</label>
                    <input type="date" class="form-control" [(ngModel)]="filtros.fechaDesde">
                  </div>
                </div>

                <div class="col-md-4">
                  <div class="form-group">
                    <label>Fecha Hasta:</label>
                    <input type="date" class="form-control" [(ngModel)]="filtros.fechaHasta">
                  </div>
                </div>

                <!-- Filtro por cliente - disponible para contratos, pagos -->
                <div class="col-md-4" *ngIf="tipoReporte === 'contratos' || tipoReporte === 'pagos'">
                  <div class="form-group">
                    <label>Cliente:</label>
                    <select class="form-control" [(ngModel)]="filtros.clienteId">
                      <option value="">Todos los clientes</option>
                      <option *ngFor="let cliente of clientes" [value]="cliente.id">
                        {{ cliente.nombre }} {{ cliente.apellidos }}
                      </option>
                    </select>
                  </div>
                </div>

                <!-- Filtro por fraccionamiento - solo para lotes -->
                <div class="col-md-4" *ngIf="tipoReporte === 'lotes'">
                  <div class="form-group">
                    <label>Fraccionamiento:</label>
                    <select class="form-control" [(ngModel)]="filtros.fraccionamientoId">
                      <option value="">Todos los fraccionamientos</option>
                      <option *ngFor="let fracc of fraccionamientos" [value]="fracc.id">
                        {{ fracc.nombre }}
                      </option>
                    </select>
                  </div>
                </div>

                <!-- Filtro por vendedor - para contratos -->
                <div class="col-md-4" *ngIf="tipoReporte === 'contratos'">
                  <div class="form-group">
                    <label>Vendedor:</label>
                    <select class="form-control" [(ngModel)]="filtros.vendedorId">
                      <option value="">Todos los vendedores</option>
                      <option *ngFor="let vendedor of vendedores" [value]="vendedor.id">
                        {{ vendedor.username }}
                      </option>
                    </select>
                  </div>
                </div>
              </div>

              <div class="row mt-3">
                <div class="col-12">
                  <button class="btn btn-primary" (click)="generarReporte()" [disabled]="loading">
                    <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                    <i *ngIf="!loading" class="fas fa-search me-2"></i>
                    {{ loading ? 'Generando...' : 'Generar Reporte' }}
                  </button>

                  <button class="btn btn-outline-secondary ms-2" (click)="limpiarFiltros()">
                    <i class="fas fa-eraser me-2"></i>Limpiar Filtros
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Resumen de resultados -->
          <div *ngIf="resumen" class="alert alert-info">
            <strong>Resumen:</strong> {{ resumen.totalRegistros }} registros encontrados
            <span *ngIf="resumen.totalLotesDisponibles !== undefined"> | Lotes Disponibles: {{ resumen.totalLotesDisponibles }}</span>
          </div>

          <!-- Resultados del reporte -->
          <div *ngIf="resultados.length > 0 && formato === 'pantalla'">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5>Resultados: {{ resultados.length }} registros</h5>
              <div>
                <button class="btn btn-sm btn-success me-2" (click)="exportarExcel()">
                  <i class="fas fa-file-excel me-2"></i>Excel
                </button>
                <button class="btn btn-sm btn-danger" (click)="exportarPDF()">
                  <i class="fas fa-file-pdf me-2"></i>PDF
                </button>
              </div>
            </div>

            <div class="table-responsive">
              <table class="table table-striped table-hover">
                <thead class="thead-dark">
                  <tr>
                    <th *ngFor="let col of columnas">{{ col.header }}</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let row of resultados">
                    <td *ngFor="let col of columnas">{{ getCellValue(row, col.field) }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Mensaje cuando no hay resultados -->
          <div *ngIf="resultados.length === 0 && reporteGenerado" class="alert alert-info">
            <i class="fas fa-info-circle me-2"></i>No se encontraron registros con los filtros seleccionados.
          </div>
        </div>
      </div>
    </div>
  `
})
export class ReportesComponent implements OnInit {
  private reporteService = inject(ReporteService);
  private clienteService = inject(ClienteService);
  private fraccService = inject(FraccionamientoService);
  private adminService = inject(AdminService);

  // Configuración
  tipoReporte = '';
  formato = 'pantalla';
  loading = false;
  reporteGenerado = false;

  // Filtros
  filtros: any = {
    fechaDesde: '',
    fechaHasta: '',
    clienteId: '',
    fraccionamientoId: '',
    vendedorId: ''
  };

  // Datos para selects
  clientes: any[] = [];
  fraccionamientos: any[] = [];
  vendedores: any[] = [];

  // Resultados
  resultados: any[] = [];
  columnas: any[] = [];
  resumen: any = null;

  ngOnInit(): void {
    this.cargarClientes();
    this.cargarFraccionamientos();
    this.cargarVendedores();
  }

  cargarClientes(): void {
    this.clienteService.getAllClientes().subscribe({
      next: (data) => this.clientes = data,
      error: (err) => console.error('Error cargando clientes:', err)
    });
  }

  cargarFraccionamientos(): void {
    this.fraccService.getAllFraccionamientos().subscribe({
      next: (data) => this.fraccionamientos = data,
      error: (err) => console.error('Error cargando fraccionamientos:', err)
    });
  }

  cargarVendedores(): void {
    this.adminService.getUsers().subscribe({
      next: (data) => {
        // Filtrar solo usuarios con rol VENDEDOR o ADMIN
        this.vendedores = data.filter((u: any) =>
          u.roles?.some((r: any) => r.name === 'ROLE_VENDEDOR' || r.name === 'ROLE_ADMIN')
        );
      },
      error: (err) => console.error('Error cargando vendedores:', err)
    });
  }

  onTipoReporteChange(): void {
    this.resultados = [];
    this.reporteGenerado = false;
    this.resumen = null;
    this.limpiarFiltros();
  }

  setFormato(fmt: string): void {
    this.formato = fmt;
    if (fmt !== 'pantalla') {
      this.generarReporte();
    }
  }

  limpiarFiltros(): void {
    this.filtros = {
      fechaDesde: '',
      fechaHasta: '',
      clienteId: '',
      fraccionamientoId: '',
      vendedorId: ''
    };
  }

  generarReporte(): void {
    this.loading = true;
    this.reporteGenerado = false;

    if (this.formato === 'excel') {
      this.reporteService.exportarExcel(this.tipoReporte, this.filtros).subscribe({
        next: (blob) => this.downloadFile(blob, `reporte_${this.tipoReporte}.xlsx`),
        error: (err) => {
          console.error('Error exportando Excel:', err);
          this.loading = false;
        },
        complete: () => this.loading = false
      });
      return;
    }

    if (this.formato === 'pdf') {
      this.reporteService.exportarPDF(this.tipoReporte, this.filtros).subscribe({
        next: (blob) => this.downloadFile(blob, `reporte_${this.tipoReporte}.pdf`),
        error: (err) => {
          console.error('Error exportando PDF:', err);
          this.loading = false;
        },
        complete: () => this.loading = false
      });
      return;
    }

    // Pantalla
    switch (this.tipoReporte) {
      case 'lotes':
        this.reporteService.getLotesReport(this.filtros).subscribe({
          next: (data) => {
            this.resumen = data;
            this.resultados = data.lotesDisponibles || [];
            this.columnas = this.getColumnasLotes();
            this.reporteGenerado = true;
          },
          error: (err) => console.error('Error:', err),
          complete: () => this.loading = false
        });
        break;

      case 'clientes':
        this.reporteService.getClientesReport(this.filtros).subscribe({
          next: (data) => {
            this.resultados = data;
            this.columnas = this.getColumnasClientes();
            this.reporteGenerado = true;
          },
          error: (err) => console.error('Error:', err),
          complete: () => this.loading = false
        });
        break;

      case 'contratos':
        this.reporteService.getContratosReport(this.filtros).subscribe({
          next: (data) => {
            this.resultados = data;
            this.columnas = this.getColumnasContratos();
            this.reporteGenerado = true;
          },
          error: (err) => console.error('Error:', err),
          complete: () => this.loading = false
        });
        break;

      case 'pagos':
        this.reporteService.getPagosReport(this.filtros).subscribe({
          next: (data) => {
            this.resultados = data;
            this.columnas = this.getColumnasPagos();
            this.reporteGenerado = true;
          },
          error: (err) => console.error('Error:', err),
          complete: () => this.loading = false
        });
        break;
    }
  }

  getColumnasLotes(): any[] {
    return [
      { field: 'id', header: 'ID' },
      { field: 'numeroLote', header: 'Lote' },
      { field: 'manzana', header: 'Manzana' },
      { field: 'fraccionamiento.nombre', header: 'Fraccionamiento' },
      { field: 'precioTotal', header: 'Precio' },
      { field: 'areaMetrosCuadrados', header: 'Área (m²)' },
      { field: 'estatus', header: 'Estatus' }
    ];
  }

  getColumnasClientes(): any[] {
    return [
      { field: 'id', header: 'ID' },
      { field: 'username', header: 'Usuario' },
      { field: 'email', header: 'Email' },
      { field: 'roles', header: 'Roles' },
      { field: 'createdAt', header: 'Fecha Registro' }
    ];
  }

  getColumnasContratos(): any[] {
    return [
      { field: 'id', header: 'ID' },
      { field: 'cliente.nombre', header: 'Cliente' },
      { field: 'lote.numeroLote', header: 'Lote' },
      { field: 'montoTotal', header: 'Monto Total' },
      { field: 'enganche', header: 'Enganche' },
      { field: 'fechaContrato', header: 'Fecha' },
      { field: 'estatus', header: 'Estatus' }
    ];
  }

  getColumnasPagos(): any[] {
    return [
      { field: 'id', header: 'ID' },
      { field: 'fechaPago', header: 'Fecha' },
      { field: 'contrato.cliente.nombre', header: 'Cliente' },
      { field: 'monto', header: 'Monto' },
      { field: 'concepto', header: 'Concepto' },
      { field: 'referencia', header: 'Referencia' },
      { field: 'metodoPago', header: 'Método' }
    ];
  }

  getCellValue(row: any, field: string): any {
    const fields = field.split('.');
    let value = row;
    for (const f of fields) {
      value = value?.[f];
      if (value === undefined || value === null) return '-';
    }

    // Formatear arrays (como roles)
    if (Array.isArray(value)) {
      return value.join(', ');
    }

    return value;
  }

  exportarExcel(): void {
    this.reporteService.exportarExcel(this.tipoReporte, this.filtros).subscribe({
      next: (blob) => this.downloadFile(blob, `reporte_${this.tipoReporte}.xlsx`),
      error: (err) => console.error('Error exportando Excel:', err)
    });
  }

  exportarPDF(): void {
    this.reporteService.exportarPDF(this.tipoReporte, this.filtros).subscribe({
      next: (blob) => this.downloadFile(blob, `reporte_${this.tipoReporte}.pdf`),
      error: (err) => console.error('Error exportando PDF:', err)
    });
  }

  downloadFile(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
