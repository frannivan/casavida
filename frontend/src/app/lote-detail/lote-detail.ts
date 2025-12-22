import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoteService } from '../services/lote';
import { ClienteService } from '../services/cliente';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lote-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lote-detail.html',
  styleUrls: ['./lote-detail.css']
})
export class LoteDetail implements OnInit {
  lote: any;
  isLoading = true;
  errorMessage = '';

  // Quote Form
  showModal = false;
  quoteData = {
    nombre: '',
    email: '',
    telefono: '',
    mensaje: ''
  };
  quoteSuccess = '';
  quoteError = '';

  constructor(
    private loteService: LoteService,
    private clienteService: ClienteService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.getLote(Number(id));
    } else {
      this.errorMessage = 'ID de lote inválido';
      this.isLoading = false;
    }
  }

  getLote(id: number): void {
    this.loteService.getLoteById(id).subscribe({
      next: (data) => {
        this.lote = data;
        // Robustness & Demo: Force distinct images as requested by user to verify carousel
        this.lote.galeriaImagenes = [
          'https://placehold.co/600x400/555/FFF?text=Foto+1',
          'https://placehold.co/600x400/2ecc71/FFF?text=Foto+2',
          'https://placehold.co/600x400/3498db/FFF?text=Foto+3',
          'https://placehold.co/600x400/e74c3c/FFF?text=Foto+4',
          'https://placehold.co/600x400/f1c40f/FFF?text=Foto+5',
          'https://placehold.co/600x400/9b59b6/FFF?text=Foto+6',
          'https://placehold.co/600x400/34495e/FFF?text=Foto+7',
          'https://placehold.co/600x400/1abc9c/FFF?text=Foto+8',
          'https://placehold.co/600x400/e67e22/FFF?text=Foto+9',
          'https://placehold.co/600x400/95a5a6/FFF?text=Foto+10'
        ];

        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar el lote: ' + err.message;
        this.isLoading = false;
        console.error(err);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/home']);
  }



  openQuoteModal(): void {
    this.showModal = true;
    this.quoteSuccess = '';
    this.quoteError = '';
    this.quoteData.mensaje = 'Hola, estoy interesado en el Lote ' + this.lote.numeroLote;
  }

  closeQuoteModal(): void {
    this.showModal = false;
  }

  // Gallery Modal
  showGalleryModal = false;
  selectedImageIndex = 0;
  displayIndex = 0;

  openGallery(index: number): void {
    this.selectedImageIndex = index;
    this.showGalleryModal = true;
  }

  closeGalleryModal(): void {
    this.showGalleryModal = false;
  }

  nextImage(): void {
    // alert('Siguiente imagen'); // DEBUG
    if (this.lote && this.lote.galeriaImagenes) {
      this.selectedImageIndex = (this.selectedImageIndex + 1) % this.lote.galeriaImagenes.length;
    }
  }

  prevImage(): void {
    if (this.lote && this.lote.galeriaImagenes) {
      this.selectedImageIndex = (this.selectedImageIndex - 1 + this.lote.galeriaImagenes.length) % this.lote.galeriaImagenes.length;
    }
  }

  submitQuote(): void {
    if (!this.quoteData.nombre || !this.quoteData.email || !this.quoteData.telefono) {
      this.quoteError = 'Por favor completa todos los campos requeridos.';
      return;
    }

    // construct lead object matching Backend Cliente entity or simply passing data
    // The backend expects Cliente entity structure.
    const lead = {
      nombre: this.quoteData.nombre,
      // Backend requires apellidos, but our simple form might just have one name field.
      // Let's split or just use same.
      apellidos: '-',
      email: this.quoteData.email,
      telefono: this.quoteData.telefono,
      // message is not in Entity but we might want to log it or ignore it for now.
      // Backend ignores extra fields in JSON if not mapped.
    };

    this.clienteService.registerLead(lead).subscribe({
      next: (res) => {
        this.quoteSuccess = res.message;
        this.quoteError = '';
        // Clear form
        this.quoteData = { nombre: '', email: '', telefono: '', mensaje: '' };
      },
      error: (err) => {
        this.quoteError = err.error?.message || 'Error al enviar solicitud.';
        console.error(err);
      }
    });
  }
  getDisplayUrl(url: string): string {
    if (!url) return 'https://placehold.co/600x400?text=Sin+Imagen';
    if (url.includes('via.placeholder.com')) {
      return url.replace('via.placeholder.com', 'placehold.co');
    }
    return url;
  }
}
