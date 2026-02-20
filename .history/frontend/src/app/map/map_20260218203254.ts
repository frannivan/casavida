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

    // 1. Plot Fraccionamientos
    const fracIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-red.png',
      shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    if (this.fraccionamientos && this.fraccionamientos.length > 0) {
      this.fraccionamientos.forEach(f => {
        // 1.1 Support for Delimiter Polygon (poligonoDelimitador)
        if (f.poligonoDelimitador && f.poligonoDelimitador.startsWith('[')) {
          try {
            const points = JSON.parse(f.poligonoDelimitador);
            if (Array.isArray(points) && points.length > 2) {
               L.polygon(points, { 
                   color: '#333', 
                   fillColor: 'transparent', 
                   weight: 2,
                   dashArray: '5, 5' 
               }).addTo(this.markersLayer);
               // Add bounds of fraccionamiento to view
               bounds.extend(L.latLngBounds(points));
               hasMarkers = true;
            }
          } catch(e) {}
        }

        // 1.2 restore Marker Logic
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
                    this.router.navigate(['/fraccionamiento', f.id]);
                  });
                }
              });

              bounds.extend([lat, lng]);
              hasMarkers = true;
           }
        }
      });
    }

    // 2. Plot Lotes
    this.lotes.forEach(lote => {
      let color = '#6c757d'; // Default Grey
      let fillColor = '#6c757d';
      
      switch(lote.estatus) {
          case 'DISPONIBLE': 
              color = '#28a745'; // Green
              fillColor = '#28a745';
              break;
          case 'VENDIDO': 
              color = '#dc3545'; // Red
              fillColor = '#dc3545';
              break;
          case 'APARTADO': 
              color = '#ffc107'; // Orange/Yellow
              fillColor = '#ffc107';
              break;
      }

      // 2.1 Plot Polygons
      if (lote.planoCoordinates && lote.planoCoordinates.startsWith('[')) {
        try {
          const points = JSON.parse(lote.planoCoordinates);
          if (Array.isArray(points) && points.length > 2) {
             const poly = L.polygon(points, { 
                color: color,
                fillColor: fillColor,
                fillOpacity: 0.6, // More visible
                weight: 1
             }).addTo(this.markersLayer);
             
             // Add tooltip to polygon too
             poly.bindTooltip(`Lote ${lote.numeroLote}`, { sticky: true });
             
             poly.on('click', () => {
                 this.router.navigate(['/lote', lote.id]);
             });
             
             bounds.extend(poly.getBounds());
             hasMarkers = true;
          }
        } catch(e) {}
      }

      // 2.2 Plot "Small Pin" (CircleMarker) with Info
      // Use coordinatesGeo (which we synced to centroid)
      if (lote.coordenadasGeo) {
        const [lat, lng] = this.parseCoords(lote.coordenadasGeo);
        if (lat && lng) {
          const marker = L.circleMarker([lat, lng], {
              radius: 6,
              fillColor: fillColor,
              color: '#fff',
              weight: 2,
              opacity: 1,
              fillOpacity: 1
          }).addTo(this.markersLayer);

          // Tooltip permanently visible on hover? User said "cuando te pares en el" -> Hover.
          marker.bindTooltip(`
              <div style="text-align: center;">
                  <strong>Lote ${lote.numeroLote}</strong><br>
                  <span class="badge ${lote.estatus === 'DISPONIBLE' ? 'badge-success' : (lote.estatus === 'VENDIDO' ? 'badge-danger' : 'badge-warning')}">${lote.estatus}</span><br>
                  ${lote.areaMetrosCuadrados} m²
              </div>
          `, { direction: 'top', offset: [0, -5] });
          
          marker.on('click', () => {
              this.router.navigate(['/lote', lote.id]);
          });
          
          if (!lote.planoCoordinates) {
             bounds.extend([lat, lng]);
             hasMarkers = true;
          }
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
