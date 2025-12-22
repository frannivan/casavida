import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LoteService } from '../services/lote';
import { FraccionamientoService } from '../services/fraccionamiento';

import { MapComponent } from '../map/map';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, MapComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent implements OnInit {
  lotes: any[] = [];
  fraccionamientos: any[] = [];
  ubicaciones: string[] = [];
  isLoading = true;

  // Filter Models
  searchFraccionamiento = '';
  searchUbicacion = '';
  sortDir = 'asc';

  constructor(
    private loteService: LoteService,
    private fraccionamientoService: FraccionamientoService
  ) { }

  ngOnInit(): void {
    this.loadFraccionamientos();
    this.search();
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
}
