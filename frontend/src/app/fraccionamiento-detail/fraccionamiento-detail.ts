import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FraccionamientoService } from '../services/fraccionamiento';
import { LoteService } from '../services/lote';
import { MapComponent } from '../map/map';

@Component({
    selector: 'app-fraccionamiento-detail',
    standalone: true,
    imports: [CommonModule, RouterModule, MapComponent],
    templateUrl: './fraccionamiento-detail.html',
    styleUrl: './fraccionamiento-detail.css'
})
export class FraccionamientoDetailComponent implements OnInit {
    public fraccionamiento: any = null;
    public lotes: any[] = [];
    public isLoading: boolean = true;
    public errorMsg: string = '';

    private route = inject(ActivatedRoute);
    private fraccionamientoService = inject(FraccionamientoService);
    private loteService = inject(LoteService);

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.loadFraccionamiento(+id);
            this.loadLotes(+id);
        } else {
            this.errorMsg = 'ID de fraccionamiento no válido.';
            this.isLoading = false;
        }
    }

    loadFraccionamiento(id: number): void {
        this.fraccionamientoService.getById(id).subscribe({
            next: data => {
                this.fraccionamiento = data;
                this.isLoading = false;
            },
            error: err => {
                console.error(err);
                this.errorMsg = 'No se pudo cargar la información del fraccionamiento.';
                this.isLoading = false;
            }
        });
    }

    loadLotes(id: number): void {
        this.loteService.getPublicLotes(id, undefined).subscribe({
            next: data => this.lotes = data,
            error: err => console.error(err)
        });
    }
}
