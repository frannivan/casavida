import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UploadService } from '../services/reporte';

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
            <button class="btn btn-outline-primary btn-sm" (click)="descargarPlantilla()" [disabled]="downloadingTemplate">
              <span *ngIf="downloadingTemplate" class="spinner-border spinner-border-sm me-2"></span>
              <i *ngIf="!downloadingTemplate" class="fas fa-download me-2"></i>
              {{ downloadingTemplate ? 'Descargando...' : 'Descargar Plantilla Excel' }}
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
            <p><strong>Total procesados:</strong> {{ resultado.totalProcesados }}</p>
            <p><strong>Exitosos:</strong> {{ resultado.exitosos }} | <strong>Errores:</strong> {{ resultado.errores }}</p>
            
            <div *ngIf="resultado.detallesErrores?.length > 0">
              <h6>Detalles de errores:</h6>
              <ul class="small">
                <li *ngFor="let error of resultado.detallesErrores">{{ error }}</li>
              </ul>
            </div>
          </div>

          <!-- Historial de cargas -->
          <div class="mt-5" *ngIf="historialCargas.length > 0">
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
  private uploadService = inject(UploadService);
  
  tipoCarga = '';
  selectedFile: File | null = null;
  uploading = false;
  downloadingTemplate = false;
  resultado: any = null;
  
  historialCargas: any[] = [];
  
  ngOnInit(): void {
    // Cargar historial desde localStorage si existe
    const saved = localStorage.getItem('cargaHistorial');
    if (saved) {
      this.historialCargas = JSON.parse(saved);
    }
  }
  
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
    if (!this.tipoCarga) return;
    
    this.downloadingTemplate = true;
    
    this.uploadService.downloadTemplate(this.tipoCarga).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `plantilla_${this.tipoCarga}.xlsx`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.downloadingTemplate = false;
      },
      error: (err) => {
        console.error('Error descargando plantilla:', err);
        this.downloadingTemplate = false;
      }
    });
  }
  
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    this.resultado = null;
  }
  
  subirArchivo(): void {
    if (!this.selectedFile || !this.tipoCarga) return;
    
    this.uploading = true;
    
    this.uploadService.uploadFile(this.tipoCarga, this.selectedFile).subscribe({
      next: (data) => {
        this.resultado = data;
        
        // Agregar al historial
        this.historialCargas.unshift({
          fecha: new Date().toLocaleString(),
          tipo: this.getTipoCargaNombre(),
          registros: data.exitosos,
          exitoso: data.errores === 0
        });
        
        // Guardar en localStorage (últimos 10)
        localStorage.setItem('cargaHistorial', JSON.stringify(this.historialCargas.slice(0, 10)));
        
        this.uploading = false;
      },
      error: (err) => {
        console.error('Error subiendo archivo:', err);
        this.resultado = {
          success: false,
          message: 'Error al procesar el archivo: ' + (err.error?.message || err.message)
        };
        this.uploading = false;
      }
    });
  }
}
