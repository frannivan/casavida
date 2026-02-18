import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

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
                <option value="ventas">📊 Reporte de Ventas</option>
              </select>
            </div>
            <div class="col-md-6">
              <label class="form-label"><strong>Formato de Exportación:</strong></label>
              <div class="btn-group w-100">
                <button class="btn" [class.btn-primary]="formato === 'pantalla'" [class.btn-outline-primary]="formato !== 'pantalla'" (click)="formato = 'pantalla'">
                  <i class="fas fa-desktop me-2"></i>Pantalla
                </button>
                <button class="btn" [class.btn-success]="formato === 'excel'" [class.btn-outline-success]="formato !== 'excel'" (click)="formato = 'excel'">
                  <i class="fas fa-file-excel me-2"></i>Excel
                </button>
                <button class="btn" [class.btn-danger]="formato === 'pdf'" [class.btn-outline-danger]="formato !== 'pdf'" (click)="formato = 'pdf'">
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
                
                <!-- Filtro por cliente - disponible para lotes, contratos, pagos -->
                <div class="col-md-4" *ngIf="mostrarFiltroCliente()">
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
                
                <!-- Filtro por estatus - solo para lotes -->
                <div class="col-md-4" *ngIf="tipoReporte === 'lotes'">
                  <div class="form-group">
                    <label>Estatus:</label>
                    <select class="form-control" [(ngModel)]="filtros.estatus">
                      <option value="">Todos</option>
                      <option value="DISPONIBLE">Disponible</option>
                      <option value="APARTADO">Apartado</option>
                      <option value="VENDIDO">Vendido</option>
                      <option value="CONTRATADO">Contratado</option>
                    </select>
                  </div>
                </div>
                
                <!-- Filtro por vendedor - para contratos y ventas -->
                <div class="col-md-4" *ngIf="tipoReporte === 'contratos' || tipoReporte === 'ventas'">
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
                    <td *ngFor="let col of columnas">{{ row[col.field] }}</td>
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
  private http = inject(HttpClient);
  
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
    estatus: '',
    vendedorId: ''
  };
  
  // Datos para selects
  clientes = [
    { id: 1, nombre: 'Juan', apellidos: 'Pérez García' },
    { id: 2, nombre: 'María', apellidos: 'López Hernández' },
    { id: 3, nombre: 'Carlos', apellidos: 'Rodríguez Martínez' }
  ];
  
  fraccionamientos = [
    { id: 1, nombre: 'Residencial Las Palmas' },
    { id: 2, nombre: 'Fraccionamiento El Sol' },
    { id: 3, nombre: 'Hacienda Real' }
  ];
  
  vendedores = [
    { id: 1, username: 'vendedor1' },
    { id: 2, username: 'vendedor2' },
    { id: 3, username: 'admin' }
  ];
  
  // Resultados
  resultados: any[] = [];
  columnas: any[] = [];
  
  ngOnInit(): void {
    // Cargar datos iniciales
    this.cargarClientes();
    this.cargarFraccionamientos();
    this.cargarVendedores();
  }
  
  cargarClientes(): void {
    // Aquí iría la llamada real al servicio
    // this.clienteService.getAll().subscribe(data => this.clientes = data);
  }
  
  cargarFraccionamientos(): void {
    // Aquí iría la llamada real al servicio
  }
  
  cargarVendedores(): void {
    // Aquí iría la llamada real al servicio
  }
  
  onTipoReporteChange(): void {
    this.resultados = [];
    this.reporteGenerado = false;
    this.limpiarFiltros();
  }
  
  mostrarFiltroCliente(): boolean {
    return ['lotes', 'contratos', 'pagos', 'ventas'].includes(this.tipoReporte);
  }
  
  limpiarFiltros(): void {
    this.filtros = {
      fechaDesde: '',
      fechaHasta: '',
      clienteId: '',
      fraccionamientoId: '',
      estatus: '',
      vendedorId: ''
    };
  }
  
  generarReporte(): void {
    this.loading = true;
    this.reporteGenerado = false;
    
    // Simular llamada al backend
    setTimeout(() => {
      this.resultados = this.generarDatosMock();
      this.columnas = this.obtenerColumnas();
      this.loading = false;
      this.reporteGenerado = true;
    }, 1000);
  }
  
  generarDatosMock(): any[] {
    const datos: any[] = [];
    
    switch (this.tipoReporte) {
      case 'lotes':
        for (let i = 1; i <= 15; i++) {
          datos.push({
            id: i,
            numeroLote: `L-${100 + i}`,
            manzana: `M-${Math.ceil(i / 5)}`,
            fraccionamiento: this.fraccionamientos[Math.floor(Math.random() * 3)].nombre,
            precio: `$${(250000 + Math.random() * 150000).toFixed(2)}`,
            area: `${(120 + Math.random() * 80).toFixed(2)} m²`,
            estatus: ['Disponible', 'Apartado', 'Vendido'][Math.floor(Math.random() * 3)],
            fechaRegistro: '2024-01-15'
          });
        }
        break;
        
      case 'clientes':
        for (let i = 1; i <= 10; i++) {
          datos.push({
            id: i,
            nombre: `Cliente ${i}`,
            email: `cliente${i}@email.com`,
            telefono: `555-${1000 + i}`,
            contratos: Math.floor(Math.random() * 3),
            totalComprado: `$${(Math.random() * 500000).toFixed(2)}`,
            fechaRegistro: '2024-02-01'
          });
        }
        break;
        
      case 'contratos':
        for (let i = 1; i <= 8; i++) {
          datos.push({
            id: i,
            folio: `C-2024-${1000 + i}`,
            cliente: this.clientes[Math.floor(Math.random() * 3)].nombre,
            lote: `L-${100 + i}`,
            montoTotal: `$${(300000 + Math.random() * 200000).toFixed(2)}`,
            enganche: `$${(30000 + Math.random() * 50000).toFixed(2)}`,
            fechaContrato: '2024-01-20',
            estatus: 'Activo'
          });
        }
        break;
        
      case 'pagos':
        for (let i = 1; i <= 20; i++) {
          datos.push({
            id: i,
            fecha: `2024-02-${10 + (i % 15)}`,
            cliente: this.clientes[Math.floor(Math.random() * 3)].nombre,
            monto: `$${(5000 + Math.random() * 10000).toFixed(2)}`,
            concepto: ['Mensualidad', 'Enganche', 'Extra'][Math.floor(Math.random() * 3)],
            referencia: `REF-${10000 + i}`,
            metodo: ['Transferencia', 'Efectivo', 'Depósito'][Math.floor(Math.random() * 3)]
          });
        }
        break;
        
      case 'ventas':
        for (let i = 1; i <= 12; i++) {
          datos.push({
            id: i,
            mes: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'][Math.floor(Math.random() * 6)],
            totalVentas: `$${(500000 + Math.random() * 1000000).toFixed(2)}`,
            numContratos: Math.floor(5 + Math.random() * 15),
            promedioVenta: `$${(200000 + Math.random() * 300000).toFixed(2)}`,
            comisiones: `$${(50000 + Math.random() * 100000).toFixed(2)}`
          });
        }
        break;
    }
    
    return datos;
  }
  
  obtenerColumnas(): any[] {
    const columnasMap: any = {
      'lotes': [
        { field: 'id', header: 'ID' },
        { field: 'numeroLote', header: 'Lote' },
        { field: 'manzana', header: 'Manzana' },
        { field: 'fraccionamiento', header: 'Fraccionamiento' },
        { field: 'precio', header: 'Precio' },
        { field: 'area', header: 'Área' },
        { field: 'estatus', header: 'Estatus' }
      ],
      'clientes': [
        { field: 'id', header: 'ID' },
        { field: 'nombre', header: 'Nombre' },
        { field: 'email', header: 'Email' },
        { field: 'telefono', header: 'Teléfono' },
        { field: 'contratos', header: 'Contratos' },
        { field: 'totalComprado', header: 'Total Comprado' }
      ],
      'contratos': [
        { field: 'id', header: 'ID' },
        { field: 'folio', header: 'Folio' },
        { field: 'cliente', header: 'Cliente' },
        { field: 'lote', header: 'Lote' },
        { field: 'montoTotal', header: 'Monto Total' },
        { field: 'enganche', header: 'Enganche' },
        { field: 'estatus', header: 'Estatus' }
      ],
      'pagos': [
        { field: 'id', header: 'ID' },
        { field: 'fecha', header: 'Fecha' },
        { field: 'cliente', header: 'Cliente' },
        { field: 'monto', header: 'Monto' },
        { field: 'concepto', header: 'Concepto' },
        { field: 'referencia', header: 'Referencia' },
        { field: 'metodo', header: 'Método' }
      ],
      'ventas': [
        { field: 'id', header: 'ID' },
        { field: 'mes', header: 'Mes' },
        { field: 'totalVentas', header: 'Total Ventas' },
        { field: 'numContratos', header: '# Contratos' },
        { field: 'promedioVenta', header: 'Promedio' },
        { field: 'comisiones', header: 'Comisiones' }
      ]
    };
    
    return columnasMap[this.tipoReporte] || [];
  }
  
  exportarExcel(): void {
    // Aquí iría la integración con ExcelService
    console.log('Exportar a Excel:', this.tipoReporte, this.filtros);
  }
  
  exportarPDF(): void {
    console.log('Exportar a PDF:', this.tipoReporte, this.filtros);
  }
}
