import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoteService } from '../services/lote';
import { FraccionamientoService } from '../services/fraccionamiento';
import { StorageService } from '../services/storage';

import { MapComponent } from '../map/map';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, MapComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  private router = inject(Router);
  private storageService = inject(StorageService);
  
  lotes: any[] = [];
  fraccionamientos: any[] = [];
  ubicaciones: string[] = [];
  isLoading = true;
  
  // Verificar si es admin
  isAdmin = false;
  


  // Filter Models
  searchFraccionamiento = '';
  searchUbicacion = '';
  sortDir = 'asc';

  constructor(
    private loteService: LoteService,
    private fraccionamientoService: FraccionamientoService
  ) { }

  ngOnInit(): void {
    this.checkAdminRole();
    this.loadFraccionamientos();
    this.search();
  }
  
  checkAdminRole(): void {
    const user = this.storageService.getUser();
    if (user && user.roles) {
      this.isAdmin = user.roles.includes('ROLE_ADMIN');
    }
  }

  loadFraccionamientos(): void {
    this.fraccionamientoService.getAllFraccionamientos().subscribe({
      next: data => {
        this.fraccionamientos = data;
        // Extract unique locations
        this.ubicaciones = [...new Set(data.map((f: any) => f.ubicacion))].filter(Boolean) as string[];
      },
      error: err => console.error(err)
    });
  }

  getDisplayUrl(url: string): string {
    if (!url) return 'https://placehold.co/300x200?text=Sin+Imagen';
    if (url.includes('via.placeholder.com')) {
      return url.replace('via.placeholder.com', 'placehold.co');
    }
    return url;
  }

  onFraccionamientoSelect(event: any): void {
    this.searchFraccionamiento = event;
    this.search();
    // Optional: Scroll to results
    const grid = document.querySelector('.grid-container');
    if (grid) grid.scrollIntoView({ behavior: 'smooth' });
  }

  search(): void {
    this.isLoading = true;
    this.loteService.getPublicLotes(this.searchFraccionamiento || null, this.searchUbicacion, this.sortDir).subscribe({
      next: data => {
        this.lotes = data;
        this.isLoading = false;
      },
      error: err => {
        console.error(err);
        this.isLoading = false;
      }
    });
  }
  
  // ================== ADMIN FUNCTIONS ==================
  
  // Eliminar fraccionamiento
  deleteFracc(fracc: any): void {
    if (confirm(`¿Estás seguro de eliminar el fraccionamiento "${fracc.nombre}"?`)) {
      this.fraccionamientoService.deleteFraccionamiento(fracc.id).subscribe({
        next: () => this.loadFraccionamientos(),
        error: err => console.error('Error eliminando fraccionamiento:', err)
      });
    }
  }

  // Eliminar lote
  deleteLote(lote: any): void {
    if (confirm(`¿Estás seguro de eliminar el lote "${lote.numeroLote}"?`)) {
      this.loteService.deleteLote(lote.id).subscribe({
        next: () => this.search(),
        error: err => console.error('Error eliminando lote:', err)
      });
    }
  }
  
  // Navegar a admin
  goToAdmin(): void {
    this.router.navigate(['/admin']);
  }
}
