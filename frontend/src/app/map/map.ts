import { Component, Input, Output, EventEmitter, OnInit, AfterViewInit, OnChanges } from '@angular/core';
import * as L from 'leaflet';
import { Router } from '@angular/router';

@Component({
  selector: 'app-map',
  templateUrl: './map.html',
  styleUrls: ['./map.css'],
  standalone: true
})
export class MapComponent implements AfterViewInit, OnChanges {
  @Input() lotes: any[] = [];
  @Input() fraccionamientos: any[] = [];
  @Output() selectFraccionamiento = new EventEmitter<any>();
  private map: any;
  private markersLayer: any;

  constructor(private router: Router) { }

  ngAfterViewInit(): void {
    this.initMap();
  }

  private initMap(): void {
    if (this.map) return; // Prevent re-init

    // Default center
    this.map = L.map('map', {
      center: [23.6345, -102.5528],
      zoom: 5
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    this.markersLayer = L.layerGroup().addTo(this.map);
    this.updateMarkers();
  }

  private updateMarkers(): void {
    if (!this.map || !this.markersLayer) return;
    this.markersLayer.clearLayers();

    const bounds = L.latLngBounds([]);
    let hasMarkers = false;

    // 1. Plot Fraccionamientos (If available)
    // Create a custom icon for Fraccionamientos (Red/Gold)
    const fracIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-gold.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    if (this.fraccionamientos && this.fraccionamientos.length > 0) {
      this.fraccionamientos.forEach(f => {
        if (f.coordenadasGeo) {
          const [lat, lng] = this.parseCoords(f.coordenadasGeo);
          if (lat && lng) {
            const marker = L.marker([lat, lng], { icon: fracIcon }).addTo(this.markersLayer);
            marker.bindPopup(`
                         <b>${f.nombre}</b><br>
                         ${f.ubicacion}<br>
                         <button class="btn btn-sm btn-warning mt-2" id="btn-frac-${f.id}">Ver Lotes</button>
                     `);
            marker.on('popupopen', () => {
              const btn = document.getElementById(`btn-frac-${f.id}`);
              if (btn) {
                btn.addEventListener('click', () => {
                  this.selectFraccionamiento.emit(f.id);
                  this.map.closePopup();
                });
              }
            });
            bounds.extend([lat, lng]);
            hasMarkers = true;
          }
        }
      });
    }

    // 2. Plot ONLY Independent Lotes (No Fraccionamiento)
    // If a lote belongs to a fraccionamiento, it is represented by the Fraccionamiento pin.
    const lotesIndependientes = this.lotes.filter(l => !l.fraccionamiento);

    lotesIndependientes.forEach(lote => {
      if (lote.coordenadasGeo) {
        const [lat, lng] = this.parseCoords(lote.coordenadasGeo);
        if (lat && lng) {
          const marker = L.marker([lat, lng]).addTo(this.markersLayer); // Default Blue
          marker.bindPopup(`
                     <b>Lote Independiente ${lote.numeroLote}</b><br>
                     $${lote.precioTotal}<br>
                     <button class="btn btn-sm btn-primary mt-2" id="btn-lote-${lote.id}">Detalles</button>
                 `);
          marker.on('popupopen', () => {
            const btn = document.getElementById(`btn-lote-${lote.id}`);
            if (btn) {
              btn.addEventListener('click', () => {
                this.router.navigate(['/lote', lote.id]);
              });
            }
          });
          bounds.extend([lat, lng]);
          hasMarkers = true;
        }
      }
    });

    if (hasMarkers) {
      this.map.fitBounds(bounds, { padding: [50, 50] });
    }
  }

  private parseCoords(coordStr: string): [number, number] | [null, null] {
    try {
      const parts = coordStr.split(',');
      if (parts.length === 2) {
        const lat = parseFloat(parts[0].trim());
        const lng = parseFloat(parts[1].trim());
        if (!isNaN(lat) && !isNaN(lng)) return [lat, lng];
      }
    } catch (e) { }
    return [null, null];
  }

  ngOnChanges(): void {
    this.updateMarkers();
  }
}
