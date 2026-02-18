import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FraccionamientoService } from '../services/fraccionamiento';
import { LoteService } from '../services/lote';
import { StorageService } from '../services/storage';
import { LocationPickerComponent } from '../components/location-picker/location-picker';

@Component({
  selector: 'app-fraccionamiento-edit',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, LocationPickerComponent],
  templateUrl: './fraccionamiento-edit.html',
  styleUrls: ['./fraccionamiento-edit.css']
})
export class FraccionamientoEditComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fraccService = inject(FraccionamientoService);
  private loteService = inject(LoteService);
  private storageService = inject(StorageService);
  
  fraccionamiento: any = null;
  lotes: any[] = [];
  isLoading = true;
  isAdmin = false;
  
  // Modos de edición
  editMode = false;
  editLoteMode = false;
  selectedLote: any = null;
  
  // Formulario de edición de fraccionamiento
  editForm: any = {
    nombre: '',
    ubicacion: '',
    descripcion: '',
    logoUrl: '',
    coordenadasGeo: ''
  };
  
  // Formulario de nuevo lote
  newLoteForm: any = {
    numeroLote: '',
    manzana: '',
    precioTotal: 0,
    areaMetrosCuadrados: 0,
    coordenadasGeo: '',
    estatus: 'DISPONIBLE',
    imagenUrl: '',
    galeriaImagenes: []
  };
  
  // Galería de fotos
  newFotoUrl = '';
  showGaleriaModal = false;
  selectedImageIndex = 0;

  ngOnInit(): void {
    this.checkAdminRole();
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadFraccionamiento(Number(id));
    }
  }
  
  checkAdminRole(): void {
    const user = this.storageService.getUser();
    if (user?.roles) {
      this.isAdmin = user.roles.includes('ROLE_ADMIN');
    }
  }
  
  loadFraccionamiento(id: number): void {
    this.isLoading = true;
    this.fraccService.getById(id).subscribe({
      next: (data) => {
        this.fraccionamiento = data;
        this.editForm = { ...data };
        this.loadLotes(id);
      },
      error: (err) => {
        console.error('Error cargando fraccionamiento:', err);
        this.isLoading = false;
      }
    });
  }
  
  loadLotes(fraccId: number): void {
    this.loteService.getLotesByFraccionamiento(fraccId).subscribe({
      next: (data) => {
        this.lotes = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error cargando lotes:', err);
        this.isLoading = false;
      }
    });
  }
  
  // ==================== EDICIÓN FRACCIONAMIENTO ====================
  
  toggleEditMode(): void {
    this.editMode = !this.editMode;
    if (!this.editMode) {
      this.editForm = { ...this.fraccionamiento };
    }
  }
  
  saveFraccionamiento(): void {
    this.fraccService.updateFraccionamiento(this.fraccionamiento.id, this.editForm).subscribe({
      next: () => {
        this.fraccionamiento = { ...this.editForm };
        this.editMode = false;
      },
      error: (err) => console.error('Error guardando:', err)
    });
  }
  
  // ==================== GESTIÓN DE LOTES ====================
  
  openNewLote(): void {
    this.selectedLote = null;
    this.editLoteMode = true;
    this.newLoteForm = {
      numeroLote: '',
      manzana: '',
      precioTotal: 0,
      areaMetrosCuadrados: 0,
      coordenadasGeo: '',
      estatus: 'DISPONIBLE',
      imagenUrl: '',
      galeriaImagenes: [],
      fraccionamiento: this.fraccionamiento
    };
  }
  
  editLote(lote: any): void {
    this.selectedLote = lote;
    this.editLoteMode = true;
    this.newLoteForm = { 
      ...lote,
      fraccionamiento: this.fraccionamiento
    };
  }
  
  saveLote(): void {
    if (this.selectedLote) {
      // Actualizar
      this.loteService.updateLote(this.selectedLote.id, this.newLoteForm).subscribe({
        next: () => {
          this.loadLotes(this.fraccionamiento.id);
          this.editLoteMode = false;
          this.selectedLote = null;
        },
        error: (err) => console.error('Error actualizando lote:', err)
      });
    } else {
      // Crear nuevo
      this.loteService.createLote(this.newLoteForm).subscribe({
        next: () => {
          this.loadLotes(this.fraccionamiento.id);
          this.editLoteMode = false;
        },
        error: (err) => console.error('Error creando lote:', err)
      });
    }
  }
  
  deleteLote(lote: any): void {
    if (confirm(`¿Eliminar lote ${lote.numeroLote}?`)) {
      this.loteService.deleteLote(lote.id).subscribe({
        next: () => this.loadLotes(this.fraccionamiento.id),
        error: (err) => console.error('Error eliminando:', err)
      });
    }
  }
  
  // ==================== GALERÍA DE FOTOS ====================
  
  addFotoToLote(): void {
    if (this.newFotoUrl.trim()) {
      if (!this.newLoteForm.galeriaImagenes) {
        this.newLoteForm.galeriaImagenes = [];
      }
      this.newLoteForm.galeriaImagenes.push(this.newFotoUrl.trim());
      this.newFotoUrl = '';
    }
  }
  
  removeFoto(index: number): void {
    this.newLoteForm.galeriaImagenes.splice(index, 1);
  }
  
  openGaleria(lote: any): void {
    this.selectedLote = lote;
    this.showGaleriaModal = true;
    this.selectedImageIndex = 0;
  }
  
  closeGaleria(): void {
    this.showGaleriaModal = false;
  }
  
  nextImage(): void {
    if (this.selectedLote?.galeriaImagenes) {
      this.selectedImageIndex = (this.selectedImageIndex + 1) % this.selectedLote.galeriaImagenes.length;
    }
  }
  
  prevImage(): void {
    if (this.selectedLote?.galeriaImagenes) {
      this.selectedImageIndex = (this.selectedImageIndex - 1 + this.selectedLote.galeriaImagenes.length) % this.selectedLote.galeriaImagenes.length;
    }
  }
  
  // ==================== UTILIDADES ====================
  
  getDisplayUrl(url: string): string {
    if (!url) return 'https://placehold.co/400x300?text=Sin+Imagen';
    if (url.includes('via.placeholder.com')) {
      return url.replace('via.placeholder.com', 'placehold.co');
    }
    return url;
  }
  
  goBack(): void {
    this.router.navigate(['/home']);
  }
  
  goToAdmin(): void {
    this.router.navigate(['/admin']);
  }
}
