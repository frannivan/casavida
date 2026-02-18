import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VentaService, CotizacionRequest } from '../services/venta';

@Component({
  selector: 'app-cotizador',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="container py-4">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <h4 class="mb-0"><i class="fas fa-calculator me-2"></i>Cotizador de Crédito</h4>
        </div>
        <div class="card-body">
          <!-- Formulario de Cotización -->
          <form (ngSubmit)="calcularCotizacion()" #cotizacionForm="ngForm">
            <div class="row">
              <div class="col-md-6">
                <div class="form-group mb-3">
                  <label>Monto Total del Lote</label>
                  <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="number" class="form-control" [(ngModel)]="cotizacion.montoTotal" 
                           name="montoTotal" required min="1" (change)="calcularEngancheSugerido()">
                  </div>
                </div>
              </div>
              
              <div class="col-md-6">
                <div class="form-group mb-3">
                  <label>Enganche (10% sugerido)</label>
                  <div class="input-group">
                    <span class="input-group-text">$</span>
                    <input type="number" class="form-control" [(ngModel)]="cotizacion.enganche" 
                           name="enganche" required min="0">
                  </div>
                </div>
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6">
                <div class="form-group mb-3">
                  <label>Plazo (meses)</label>
                  <select class="form-control" [(ngModel)]="cotizacion.plazoMeses" name="plazoMeses" required>
                    <option [value]="12">12 meses (1 año)</option>
                    <option [value]="24">24 meses (2 años)</option>
                    <option [value]="36">36 meses (3 años)</option>
                    <option [value]="48">48 meses (4 años)</option>
                    <option [value]="60">60 meses (5 años)</option>
                    <option [value]="72">72 meses (6 años)</option>
                    <option [value]="120">120 meses (10 años)</option>
                  </select>
                </div>
              </div>
              
              <div class="col-md-6">
                <div class="form-group mb-3">
                  <label>Tasa Anual (%)</label>
                  <div class="input-group">
                    <input type="number" class="form-control" [(ngModel)]="cotizacion.tasaAnual" 
                           name="tasaAnual" required min="0" max="100" step="0.1">
                    <span class="input-group-text">%</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6">
                <div class="alert alert-info">
                  <strong>Monto a Financiar:</strong> 
                  <h5 class="mb-0">{{ getMontoFinanciar() | currency:'MXN' }}</h5>
                </div>
              </div>
              <div class="col-md-6">
                <button type="submit" class="btn btn-primary btn-lg w-100" [disabled]="loading">
                  <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
                  <i *ngIf="!loading" class="fas fa-calculator me-2"></i>
                  {{ loading ? 'Calculando...' : 'Calcular Cotización' }}
                </button>
              </div>
            </div>
          </form>
          
          <!-- Resumen de la Cotización -->
          <div *ngIf="resumenCotizacion" class="mt-4">
            <div class="card bg-light">
              <div class="card-body">
                <h5 class="card-title">Resumen de la Cotización</h5>
                <div class="row">
                  <div class="col-md-3">
                    <div class="text-center">
                      <small class="text-muted">Precio Total</small>
                      <h5>{{ cotizacion.montoTotal | currency:'MXN' }}</h5>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="text-center">
                      <small class="text-muted">Enganche</small>
                      <h5>{{ cotizacion.enganche | currency:'MXN' }}</h5>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="text-center">
                      <small class="text-muted">Monto a Financiar</small>
                      <h5 class="text-primary">{{ resumenCotizacion.montoFinanciar | currency:'MXN' }}</h5>
                    </div>
                  </div>
                  <div class="col-md-3">
                    <div class="text-center">
                      <small class="text-muted">Pago Mensual</small>
                      <h5 class="text-success">{{ resumenCotizacion.pagoMensual | currency:'MXN' }}</h5>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Tabla de Amortización -->
          <div *ngIf="tablaAmortizacion.length > 0" class="mt-4">
            <h5>Tabla de Amortización</h5>
            <div class="table-responsive">
              <table class="table table-striped table-hover">
                <thead class="thead-dark">
                  <tr>
                    <th># Pago</th>
                    <th>Fecha</th>
                    <th>Pago Mensual</th>
                    <th>Capital</th>
                    <th>Interés</th>
                    <th>Saldo</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let row of tablaAmortizacion; let i = index"
                      [ngClass]="{'table-success': i === 0}">
                    <td>{{ row.numeroPago }}</td>
                    <td>{{ row.fechaPago }}</td>
                    <td>{{ row.pagoMensual | currency:'MXN' }}</td>
                    <td>{{ row.capital | currency:'MXN' }}</td>
                    <td>{{ row.interes | currency:'MXN' }}</td>
                    <td>{{ row.saldo | currency:'MXN' }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
            
            <!-- Botones de acción -->
            <div class="mt-3 text-right">
              <button class="btn btn-outline-primary me-2" (click)="imprimirCotizacion()">
                <i class="fas fa-print me-2"></i>Imprimir
              </button>
              <button class="btn btn-success" (click)="generarContrato()">
                <i class="fas fa-file-signature me-2"></i>Generar Contrato
              </button>
            </div>
          </div>
          
        </div>
      </div>
    </div>
  `,
  styles: [`
    .table-responsive {
      max-height: 500px;
      overflow-y: auto;
    }
  `]
})
export class CotizadorComponent implements OnInit {
  private ventaService = inject(VentaService);
  
  cotizacion: CotizacionRequest = {
    montoTotal: 250000,
    enganche: 25000,
    plazoMeses: 60,
    tasaAnual: 12
  };
  
  loading = false;
  tablaAmortizacion: any[] = [];
  resumenCotizacion: any = null;
  
  ngOnInit(): void {
    this.calcularEngancheSugerido();
  }
  
  calcularEngancheSugerido(): void {
    // Sugerir 10% de enganche
    this.cotizacion.enganche = Math.round(this.cotizacion.montoTotal * 0.10);
  }
  
  getMontoFinanciar(): number {
    return this.cotizacion.montoTotal - this.cotizacion.enganche;
  }
  
  calcularCotizacion(): void {
    if (this.cotizacion.montoTotal <= 0 || this.cotizacion.plazoMeses <= 0) {
      return;
    }
    
    this.loading = true;
    
    this.ventaService.cotizar(this.cotizacion).subscribe({
      next: (data) => {
        this.tablaAmortizacion = data;
        this.calcularResumen();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error al calcular cotización:', err);
        this.loading = false;
      }
    });
  }
  
  calcularResumen(): void {
    if (this.tablaAmortizacion.length === 0) return;
    
    const montoFinanciar = this.getMontoFinanciar();
    const pagoMensual = this.tablaAmortizacion[0]?.pagoMensual || 0;
    
    this.resumenCotizacion = {
      montoFinanciar: montoFinanciar,
      pagoMensual: pagoMensual,
      totalPagos: this.tablaAmortizacion.length
    };
  }
  
  imprimirCotizacion(): void {
    window.print();
  }
  
  generarContrato(): void {
    // Redirigir a la página de generar contrato con los datos precargados
    // Esto se puede implementar con navegación y query params
    console.log('Generar contrato con datos:', this.cotizacion);
  }
}
