import { Component, OnInit, Input, AfterViewInit, OnDestroy, ChangeDetectorRef, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import * as L from 'leaflet';

@Component({
    selector: 'app-polygon-editor',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './polygon-editor.component.html',
    styleUrls: ['./polygon-editor.component.css']
})
export class PolygonEditorComponent implements OnInit, AfterViewInit, OnDestroy {

    @Input() fraccionamientoId: number | null = null;

    // State
    activeTab: 'fraccionamiento' | 'lotes' = 'fraccionamiento';

    // Data
    fraccionamientos: any[] = [];
    lotes: any[] = [];

    // Selected items
    selectedFraccionamientoId: any = '';
    selectedFraccionamiento: any = null;
    selectedLoteId: any = '';
    selectedLote: any = null;

    // Map related
    private map: L.Map | undefined;
    private markersLayer: L.LayerGroup = L.layerGroup();
    private polygonLayer: L.LayerGroup = L.layerGroup(); // For saved polygons
    private drawLayer: L.LayerGroup = L.layerGroup(); // For current drawing
    
    // Drawing State
    currentPoints: L.LatLng[] = [];
    tempPolyline: L.Polyline | undefined;
    tempPolygon: L.Polygon | undefined;

    constructor(private http: HttpClient, private router: Router, private cdr: ChangeDetectorRef) { }

    ngOnInit(): void {
        if (this.fraccionamientoId) {
            this.selectedFraccionamientoId = this.fraccionamientoId;
            this.loadFraccionamientoData();
        } else {
            this.loadFraccionamientos();
        }
    }

    ngAfterViewInit(): void {
        // Init map if tab is already active (e.g. reload)
        // Delay slightly to ensure container is ready
        setTimeout(() => {
            this.initMap();
        }, 100);
    }

    ngOnDestroy(): void {
        if (this.map) {
            this.map.remove();
        }
    }

    logout(): void {
        localStorage.clear();
        sessionStorage.clear();
        document.cookie.split(";").forEach(function(c) {
          document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
        });
        this.router.navigate(['/home']);
        setTimeout(() => window.location.reload(), 100);
    }

    onTabChange(tab: 'fraccionamiento' | 'lotes'): void {
        console.log("DEBUG: onTabChange called with:", tab);
        this.activeTab = tab;
        this.resetMap();
        
        if (tab === 'lotes' && this.selectedFraccionamientoId && this.lotes.length === 0) {
            this.loadLotesForFraccionamiento();
        }
    }

    resetMap(): void {
        // Destroy existing map and re-init for the new container
        if (this.map) {
            this.map.remove();
            this.map = undefined;
        }
        this.currentPoints = [];
        this.drawLayer.clearLayers();
        this.markersLayer.clearLayers();
        this.polygonLayer.clearLayers();
        
        setTimeout(() => {
            this.initMap();
        }, 100);
    }

    initMap(): void {
        const mapId = this.activeTab === 'fraccionamiento' ? 'map-fraccionamiento' : 'map-lotes';
        const element = document.getElementById(mapId);
        
        if (!element) return;

        this.map = L.map(mapId).setView([23.6345, -102.5528], 5); // Default Mexico

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '© OpenStreetMap contributors'
        }).addTo(this.map);

        this.markersLayer.addTo(this.map);
        this.polygonLayer.addTo(this.map);
        this.drawLayer.addTo(this.map);

        // Map Click Handler for Drawing
        this.map.on('click', (e: L.LeafletMouseEvent) => {
            this.handleMapClick(e.latlng);
        });

        // Restore view if fraccionamiento selected
        if (this.selectedFraccionamiento) {
            this.centerMapOnFraccionamiento();
            this.displayExistingPolygons();
        }
    }

    handleMapClick(latlng: L.LatLng): void {
        // If we have a saved polygon and we are not explicitly clearing/editing, maybe prevent?
        // But user can "Clear Points" to start over.
        // If saved polygon exists, warn user?
        if (this.activeTab === 'fraccionamiento' && this.selectedFraccionamiento?.poligonoDelimitador && this.currentPoints.length === 0) {
           console.log("Map click ignored: Existing fracc polygon. Use 'Nuevo Polígono' button.");
           return;
        }
        
        if (this.activeTab === 'lotes' && this.selectedLote?.planoCoordinates && this.currentPoints.length === 0) {
             console.log("Map click ignored: Existing lote polygon. Use 'Nuevo Polígono' button.");
             return;
        }
        
        if (this.activeTab === 'lotes' && !this.selectedLote) {
            alert("Primero selecciona un Lote para editar.");
            return;
        }

        this.currentPoints = [...this.currentPoints, latlng];
        this.updateDrawLayers();
        this.cdr.detectChanges();
    }

    updateDrawLayers(): void {
        this.drawLayer.clearLayers();

        // Draw Markers (Points) - Using CircleMarker for "dots"
        this.currentPoints.forEach((p, index) => {
            const marker = L.circleMarker(p, { 
                radius: 6,
                fillColor: '#fff',
                color: '#3388ff',
                weight: 2,
                opacity: 1,
                fillOpacity: 1
            }).addTo(this.drawLayer);
            
            marker.on('click', () => {
                // Remove point on click
                this.currentPoints.splice(index, 1);
                this.updateDrawLayers();
            });
        });

        // Draw Polyline/Polygon
        if (this.currentPoints.length > 1) {
            L.polyline(this.currentPoints, { color: 'blue' }).addTo(this.drawLayer);
        }
        if (this.currentPoints.length > 2) {
             L.polygon(this.currentPoints, { color: 'blue', fillOpacity: 0.1, stroke: false }).addTo(this.drawLayer);
        }
    }
    
    clearPoints(): void {
        console.log("DEBUG: clearPoints called");
        this.currentPoints = [];
        this.updateDrawLayers();
        this.cdr.detectChanges();
    }

    startNewFraccionamientoDrawing(): void {
        console.log("DEBUG: startNewFraccionamientoDrawing called");
        this.selectedFraccionamiento.poligonoDelimitador = null;
        this.polygonLayer.clearLayers();
        this.currentPoints = [];
        this.updateDrawLayers();
        this.cdr.detectChanges();
    }

    startNewLoteDrawing(): void {
        console.log("DEBUG: startNewLoteDrawing called");
        this.selectedLote.planoCoordinates = null;
        this.polygonLayer.clearLayers();
        this.displayExistingPolygons(); // This will redraw others but NOT the current one as we cleared its coordinates
        this.currentPoints = [];
        this.updateDrawLayers();
        this.cdr.detectChanges();
    }

    // === DATA LOADING ===

    loadFraccionamientos(): void {
        this.http.get<any[]>(`${environment.apiUrl}/fraccionamientos/public`).subscribe(data => {
            this.fraccionamientos = data;
        });
    }

    loadFraccionamientoData(): void {
        if (!this.selectedFraccionamientoId) {
            this.selectedFraccionamiento = null;
            this.currentPoints = [];
            this.lotes = [];
            if(this.map) {
                 this.polygonLayer.clearLayers();
                 this.drawLayer.clearLayers();
            }
            return;
        }

        this.http.get(`${environment.apiUrl}/fraccionamientos/public/${this.selectedFraccionamientoId}`)
            .subscribe((fracc: any) => {
                this.selectedFraccionamiento = fracc;
                
                // Ensure map initializes after DOM update (since container depends on *ngIf=selectedFraccionamiento)
                setTimeout(() => {
                    if (!this.map) {
                        this.initMap();
                    }
                    this.centerMapOnFraccionamiento();
                    
                    // Display existing polygons after map is ready
                    this.displayExistingPolygons();
                }, 200);
                
                // Load lotes if needed
                if (this.activeTab === 'lotes' || this.fraccionamientoId) {
                    this.loadLotesForFraccionamiento();
                }
            });
    }

    loadLotesForFraccionamiento(): void {
        if (!this.selectedFraccionamientoId) {
            this.lotes = [];
            this.selectedLote = null;
            return;
        }

        this.http.get(`${environment.apiUrl}/lotes/adm/by-fraccionamiento/${this.selectedFraccionamientoId}`)
            .subscribe((lotes: any) => {
                this.lotes = lotes;
                this.displayExistingPolygons();
            });
    }

    loadLoteData(): void {
       this.currentPoints = [];
       this.drawLayer.clearLayers();
       this.selectedLote = this.lotes.find(l => l.id == this.selectedLoteId);
       
       if (this.selectedLote && this.selectedLote.planoCoordinates) {
           this.displayExistingPolygons();
       }
    }

    // === MAP HELPERS ===
    
    centerMapOnFraccionamiento(): void {
        if (!this.map || !this.selectedFraccionamiento) return;
        
        // Try to parse coordenadas_geo "lat, lng"
        if (this.selectedFraccionamiento.coordenadasGeo) {
            try {
                const parts = this.selectedFraccionamiento.coordenadasGeo.split(',');
                if (parts.length === 2) {
                    const lat = parseFloat(parts[0].trim());
                    const lng = parseFloat(parts[1].trim());
                    if (!isNaN(lat) && !isNaN(lng)) {
                        this.map.setView([lat, lng], 16);
                        return;
                    }
                }
            } catch (e) {
                console.error("Error parsing coords", e);
            }
        }
        
        // Fallback: if polygon exists, bounds of polygon
        if (this.selectedFraccionamiento.poligonoDelimitador) {
            try {
                const points = JSON.parse(this.selectedFraccionamiento.poligonoDelimitador);
                if (Array.isArray(points) && points.length > 0 && Array.isArray(points[0])) {
                     const bounds = L.latLngBounds(points);
                     this.map.fitBounds(bounds);
                }
            } catch (e) {}
        }
    }
    
    displayExistingPolygons(): void {
        if (!this.map) return;
        this.polygonLayer.clearLayers();
        
        // 1. Fraccionamiento Polygon
        if (this.selectedFraccionamiento && this.selectedFraccionamiento.poligonoDelimitador) {
            try {
                const points = JSON.parse(this.selectedFraccionamiento.poligonoDelimitador);
                // Check if it's the new format [[lat,lng],...] (Array of Arrays)
                // Old format was [{x,y}] (Array of Objects). If old format, ignore or warn.
                if (Array.isArray(points) && points.length > 0 && Array.isArray(points[0])) {
                    L.polygon(points as L.LatLngExpression[], { 
                        color: 'red', 
                        fillOpacity: 0.1, 
                        weight: 2 
                    }).addTo(this.polygonLayer);
                }
            } catch (e) { console.error("Error display fracc poly", e); }
        }
        
        // 2. Lotes Polygons (If in Lotes tab)
        if (this.activeTab === 'lotes') {
            this.lotes.forEach(lote => {
                if (lote.planoCoordinates) {
                     try {
                        const points = JSON.parse(lote.planoCoordinates);
                         if (Array.isArray(points) && points.length > 0 && Array.isArray(points[0])) {
                             const isSelected = this.selectedLote && this.selectedLote.id === lote.id;
                             L.polygon(points as L.LatLngExpression[], { 
                                color: isSelected ? 'blue' : 'gray', 
                                fillColor: isSelected ? 'cyan' : 'gray',
                                fillOpacity: isSelected ? 0.3 : 0.1, 
                                weight: 1
                            }).addTo(this.polygonLayer);
                         }
                    } catch (e) { }
                }
            });
        }
    }

    zoomToFraccionamiento(): void {
        this.centerMapOnFraccionamiento();
    }


    // === SAVING ===

    saveFraccionamientoPolygon(): void {
        console.log("DEBUG: saveFraccionamientoPolygon called. Points:", this.currentPoints.length);
        // Force an alert to confirm the click reached the code
        if (this.currentPoints.length < 3) {
            alert('❌ No se puede guardar: Se requieren al menos 3 puntos. Tienes: ' + this.currentPoints.length);
            return;
        }

        if (!confirm('¿Deseas guardar este polígono para el fraccionamiento?')) return;

        // Convert LatLng objects to Array of Arrays [[lat,lng], ...] to be JSON serializable
        const pointsArray = this.currentPoints.map(p => [p.lat, p.lng]);
        const polygonJson = JSON.stringify(pointsArray);

        this.http.put(
            `${environment.apiUrl}/fraccionamientos/adm/${this.selectedFraccionamiento.id}/poligono`,
            polygonJson,
            { headers: { 'Content-Type': 'text/plain' } }
        ).subscribe({
            next: () => {
                alert('Polígono del fraccionamiento guardado exitosamente');
                this.selectedFraccionamiento.poligonoDelimitador = polygonJson;
                this.currentPoints = [];
                this.updateDrawLayers();
                this.displayExistingPolygons();
                this.cdr.detectChanges();
            },
            error: (err) => {
                console.error("Error saving fracc polygon:", err);
                alert('No se pudo guardar el polígono. Revisa la consola para más detalles.');
            }
        });
    }

    deleteFraccionamientoPolygon(): void {
        if (!confirm('¿Eliminar el polígono del fraccionamiento?')) return;

        this.http.delete(
            `${environment.apiUrl}/fraccionamientos/adm/${this.selectedFraccionamiento.id}/poligono`
        ).subscribe(() => {
            alert('Polígono eliminado');
            this.selectedFraccionamiento.poligonoDelimitador = null;
            this.displayExistingPolygons();
        });
    }
    
    // === LOTES SAVING ===
    
    calculateCentroid(points: L.LatLng[]): string {
        if (!points || points.length === 0) return '';
        let latSum = 0;
        let lngSum = 0;
        points.forEach(p => {
            latSum += p.lat;
            lngSum += p.lng;
        });
        const centerLat = latSum / points.length;
        const centerLng = lngSum / points.length;
        return `${centerLat.toFixed(6)}, ${centerLng.toFixed(6)}`;
    }
    
    saveLotePolygon(): void {
        console.log("DEBUG: saveLotePolygon called. Points:", this.currentPoints.length);
        if (!this.selectedLote) {
            alert('❌ No hay un lote seleccionado.');
            return;
        }
        if (this.currentPoints.length < 3) {
            alert('❌ No se puede guardar: Se requieren al menos 3 puntos. Tienes: ' + this.currentPoints.length);
            return;
        }

        if (!confirm('¿Deseas guardar el polígono para el lote ' + this.selectedLote.numeroLote + '?')) return;

        const pointsArray = this.currentPoints.map(p => [p.lat, p.lng]);
        const polygonJson = JSON.stringify(pointsArray);

        // 1. Save Polygon
        this.http.put(
            `${environment.apiUrl}/lotes/adm/${this.selectedLote.id}/poligono`,
            polygonJson,
            { headers: { 'Content-Type': 'text/plain' } }
        ).subscribe({
            next: () => {
                // 2. Update Location (Centroid) to fix "wrong pin"
                const newCentroid = this.calculateCentroid(this.currentPoints);
                if (newCentroid) {
                    this.selectedLote.coordenadasGeo = newCentroid;
                    this.selectedLote.planoCoordinates = polygonJson;
                    
                    this.http.put(
                        `${environment.apiUrl}/lotes/${this.selectedLote.id}`,
                        this.selectedLote
                    ).subscribe({
                        next: () => {
                            alert('Polígono y Ubicación (Pin) actualizados correctamente');
                            this.currentPoints = [];
                            this.updateDrawLayers();
                            this.displayExistingPolygons();
                            this.cdr.detectChanges();
                        },
                        error: (err) => {
                            console.error("Error updating lote location:", err);
                            alert('Polígono guardado, pero error al actualizar ubicación del pin.');
                        }
                    });
                } else {
                    alert('Polígono del lote guardado exitosamente');
                    this.selectedLote.planoCoordinates = polygonJson;
                    this.currentPoints = [];
                    this.updateDrawLayers();
                    this.displayExistingPolygons();
                    this.cdr.detectChanges();
                }
            },
            error: (err) => {
                console.error("Error saving lote polygon:", err);
                alert('No se pudo guardar el polígono del lote.');
            }
        });
    }
    
    deleteLotePolygon(): void {
         if (!this.selectedLote) return;
         if (!confirm('¿Eliminar el polígono de este lote?')) return;
         
         this.http.put(
            `${environment.apiUrl}/lotes/adm/${this.selectedLote.id}/poligono`,
            '',
             { headers: { 'Content-Type': 'text/plain' } }
        ).subscribe(() => {
            alert('Polígono eliminado');
            this.selectedLote.planoCoordinates = null;
            this.displayExistingPolygons();
        });
    }
    
    updateLoteEstatus(): void {
        this.http.put(
            `${environment.apiUrl}/lotes/adm/${this.selectedLote.id}/estatus`,
            { estatus: this.selectedLote.estatus }
        ).subscribe(() => {
            alert('Estatus actualizado');
        });
    }
}
