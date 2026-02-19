import { Component, OnInit, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FraccionamientoService } from '../../../services/fraccionamiento';
import { LoteService } from '../../../services/lote';

import { StorageService } from '../../../services/storage';

@Component({
  selector: 'app-fraccionamiento-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './fraccionamiento-list.component.html'
})
export class FraccionamientoListComponent implements OnInit {
  @Input() readOnly = false;
  
  fraccionamientos: any[] = [];
  
  // Create / Edit logic
  isCreatingFracc = false;
  newFracc: any = {
    nombre: '',
    ubicacion: '',
    descripcion: '',
    logoUrl: '',
    coordenadasGeo: ''
  };

  // Detail Modal
  showFraccDetailModal = false;
  selectedFracc: any = null;
  fraccLotes: any[] = [];
  
  private fraccionamientoService = inject(FraccionamientoService);
  private loteService = inject(LoteService);
  private storageService = inject(StorageService);

  ngOnInit(): void {
    const user = this.storageService.getUser();
    if (user && user.roles.includes('ROLE_VENDEDOR')) {
        this.readOnly = true;
    }
    this.loadFraccionamientos();
  }

  loadFraccionamientos(): void {
    this.fraccionamientoService.getAllFraccionamientos().subscribe({
      next: data => this.fraccionamientos = data,
      error: err => console.error(err)
    });
  }

  onCreateFracc(): void {
    if (this.readOnly) return;
    this.fraccionamientoService.createFraccionamiento(this.newFracc).subscribe({
      next: res => {
          this.isCreatingFracc = false;
          this.loadFraccionamientos();
          this.newFracc = { nombre: '', ubicacion: '', descripcion: '', logoUrl: '', coordenadasGeo: '' };
      },
      error: err => console.error(err)
    });
  }

  viewFraccDetail(fracc: any): void {
      this.selectedFracc = fracc;
      this.showFraccDetailModal = true;
      this.loteService.getLotesByFraccionamiento(fracc.id).subscribe({
          next: data => this.fraccLotes = data,
          error: err => console.error(err)
      });
  }

  deleteFracc(id: number): void {
      if (this.readOnly) return;
      if (confirm('¿Seguro de eliminar este fraccionamiento?')) {
          this.fraccionamientoService.deleteFraccionamiento(id).subscribe({
              next: () => this.loadFraccionamientos(),
              error: err => console.error(err)
          });
      }
  }
}
