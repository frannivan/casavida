import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-carga-masiva',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0"><i class="fas fa-file-upload me-2"></i>Carga Masiva de Datos</h4>
        </div>
        <div class="card-body">
          <!-- Selección de tipo de carga -->
          <div class="mb-4">
            <label class="form-label"><strong>Tipo de Carga:</strong></label>
            <select class="form-control" [(ngModel)]="tipoCarga" (change)="onTipoCargaChange()">
              <option value="">-- Seleccionar tipo --</option>
              <option value="lotes">🏠 Lotes/Terrenos</option>
              <option value="clientes">👥 Clientes</option>
              <option value="contratos">📝 Contratos</option>
              <option value="pagos">💳 Pagos</option>
            </select>
          </div>

          <!-- Info y plantilla -->
          <div *ngIf="tipoCarga" class="alert alert-info">
            <h5>{{ getTipoCargaNombre() }}</h5>
            <p>{{ getTipoCargaDescripcion() }}</p>
            <button class="btn btn-outline-primary btn-sm" (click)="descargarPlantilla()">
              <i class="fas fa-download me-2"></i>Descargar Plantilla Excel
            </button>
          </div>

          <!-- Upload -->
          <div *ngIf="tipoCarga" class="mt-4">
            <label class="form-label"><strong>Seleccionar archivo Excel:</strong></label>
            <div class="input-group mb-3">
              <input type="file" class="form-control" accept=".xlsx,.xls" (change)="onFileSelected($event)">
              <button class="btn btn-success" [disabled]="!selectedFile || uploading" (click)="subirArchivo()">
                <span *ngIf="uploading" class="spinner-border spinner-border-sm me-2"></span>
                {{ uploading ? 'Subiendo...' : 'Subir y Procesar' }}
              </button>
            </div>
            <small class="text-muted">Formatos aceptados: .xlsx, .xls (máx. 10MB)</small>
          </div>

          <!-- Resultado -->
          <div *ngIf="resultado" class="mt-4" [class]="resultado.success ? 'alert alert-success' : 'alert alert-danger'">
            <h5>{{ resultado.success ? '✅ Carga Exitosa' : '❌ Error en Carga' }}</h5>
            <p>{{ resultado.message }}</p>
            <div *ngIf="resultado.details">
              <ul>
                <li *ngFor="let detail of resultado.details">{{ detail }}</li>
              </ul>
            </div>
          </div>

          <!-- Historial de cargas -->
          <div class="mt-5">
            <h5>Historial de Cargas</h5>
            <div class="table-responsive">
              <table class="table table-sm table-striped">
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Tipo</th>
                    <th>Registros</th>
                    <th>Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let carga of historialCargas">
                    <td>{{ carga.fecha }}</td>
                    <td>{{ carga.tipo }}</td>
                    <td>{{ carga.registros }}</td>
                    <td>
                      <span class="badge" [class.bg-success]="carga.exitoso" [class.bg-danger]="!carga.exitoso">
                        {{ carga.exitoso ? 'Exitoso' : 'Con Errores' }}
                      </span>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class CargaMasivaComponent implements OnInit {
  private http = inject(HttpClient);
  
  tipoCarga = '';
  selectedFile: File | null = null;
  uploading = false;
  resultado: any = null;
  
  historialCargas = [
    { fecha: '2024-02-15 10:30', tipo: 'Lotes', registros: 25, exitoso: true },
    { fecha: '2024-02-10 14:20', tipo: 'Clientes', registros: 10, exitoso: true },
    { fecha: '2024-02-05 09:15', tipo: 'Contratos', registros: 5, exitoso: false }
  ];
  
  ngOnInit(): void {}
  
  onTipoCargaChange(): void {
    this.selectedFile = null;
    this.resultado = null;
  }
  
  getTipoCargaNombre(): string {
    const nombres: any = {
      'lotes': 'Carga de Lotes/Terrenos',
      'clientes': 'Carga de Clientes',
      'contratos': 'Carga de Contratos',
      'pagos': 'Carga de Pagos'
    };
    return nombres[this.tipoCarga] || '';
  }
  
  getTipoCargaDescripcion(): string {
    const descripciones: any = {
      'lotes': 'Carga masiva de lotes con información de número, manzana, precio, área y fraccionamiento.',
      'clientes': 'Carga masiva de clientes con nombre, apellidos, email, teléfono y dirección.',
      'contratos': 'Carga masiva de contratos vinculando clientes y lotes existentes.',
      'pagos': 'Carga masiva de pagos aplicados a contratos existentes.'
    };
    return descripciones[this.tipoCarga] || '';
  }
  
  descargarPlantilla(): void {
    // Generar plantilla Excel según el tipo
    const plantillas: any = {
      'lotes': ['numero_lote', 'manzana', 'precio', 'area_m2', 'fraccionamiento_id', 'coordenadas'],
      'clientes': ['nombre', 'apellidos', 'email', 'telefono', 'direccion', 'rfc'],
      'contratos': ['cliente_id', 'lote_id', 'monto_total', 'enganche', 'plazo_meses', 'tasa_anual'],
      'pagos': ['contrato_id', 'fecha_pago', 'monto', 'concepto', 'referencia', 'metodo_pago']
    };
    
    const headers = plantillas[this.tipoCarga];
    const csvContent = headers.join(',') + '\n';
    
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `plantilla_${this.tipoCarga}.csv`;
    a.click();
  }
  
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }
  
  subirArchivo(): void {
    if (!this.selectedFile) return;
    
    this.uploading = true;
    
    // Simular upload - aquí iría la llamada real al backend
    setTimeout(() => {
      this.resultado = {
        success: true,
        message: `Se procesaron 25 registros de ${this.getTipoCargaNombre()} exitosamente.`,
        details: ['10 registros nuevos', '15 registros actualizados', '0 errores']
      };
      this.uploading = false;
    }, 2000);
  }
}
