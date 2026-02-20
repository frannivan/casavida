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
  isEditingFracc = false;
  editingId: number | null = null;
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
    
    if (this.isEditingFracc && this.editingId) {
        this.fraccionamientoService.updateFraccionamiento(this.editingId, this.newFracc).subscribe({
            next: res => {
                this.resetForm();
                this.loadFraccionamientos();
            },
            error: err => console.error(err)
        });
    } else {
        this.fraccionamientoService.createFraccionamiento(this.newFracc).subscribe({
          next: res => {
              this.resetForm();
              this.loadFraccionamientos();
          },
          error: err => console.error(err)
        });
    }
  }

  editFracc(fracc: any): void {
      if (this.readOnly) return;
      this.isEditingFracc = true;
      this.isCreatingFracc = true;
      this.editingId = fracc.id;
      this.newFracc = { ...fracc };
  }

  resetForm(): void {
      this.isCreatingFracc = false;
      this.isEditingFracc = false;
      this.editingId = null;
      this.newFracc = { nombre: '', ubicacion: '', descripcion: '', logoUrl: '', coordenadasGeo: '' };
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
