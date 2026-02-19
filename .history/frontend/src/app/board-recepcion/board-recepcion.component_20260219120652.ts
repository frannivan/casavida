import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PagoService, Pago } from '../services/pago';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-board-recepcion',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './board-recepcion.component.html',
  styleUrls: ['./board-recepcion.component.css']
})
export class BoardRecepcionComponent implements OnInit {
  pagos: Pago[] = [];
  filteredPagos: Pago[] = [];
  searchTerm: string = '';
  statusFilter: string = 'PENDIENTE'; // Default to pending
  message: string = '';
  
  private pagoService = inject(PagoService);

  ngOnInit(): void {
    this.loadPagos();
  }

  loadPagos(): void {
    this.pagoService.getAllPagos().subscribe({
      next: (data: Pago[]) => {
        this.pagos = data;
        this.filterPagos();
      },
      error: (err: any) => {
        console.error('Error loading payments', err);
        this.message = 'Error al cargar pagos.';
      }
    });
  }

  filterPagos(): void {
    let temp = this.pagos;

    if (this.statusFilter !== 'ALL') {
      temp = temp.filter(p => (p.estatus || 'PENDIENTE') === this.statusFilter);
    }

    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      temp = temp.filter(p => 
        (p.referencia?.toLowerCase() || '').includes(term) ||
        (p.concepto?.toLowerCase() || '').includes(term) ||
        (String(p.monto) || '').includes(term)
      );
    }

    this.filteredPagos = temp;
  }

  validatePayment(id: number): void {
    if (confirm('¿Confirmar validación de este pago?')) {
      this.pagoService.validatePago(id, 'VALIDADO').subscribe({
        next: (res: any) => {
          this.message = 'Pago validado correctamente.';
          this.loadPagos();
          setTimeout(() => this.message = '', 3000);
        },
        error: (err: any) => {
          this.message = 'Error al validar pago: ' + (err.message || 'Error desconocido');
        }
      });
    }
  }

  rejectPayment(id: number): void {
      if (confirm('¿Rechazar este pago?')) {
        this.pagoService.validatePago(id, 'RECHAZADO').subscribe({
          next: (res: any) => {
            this.message = 'Pago rechazado.';
            this.loadPagos();
            setTimeout(() => this.message = '', 3000);
          },
          error: (err: any) => {
            this.message = 'Error al rechazar pago: ' + (err.message || 'Error desconocido');
          }
        });
      }
    }
}
