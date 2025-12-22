import { Component, EventEmitter, Input, Output, AfterViewInit, OnDestroy } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-location-picker',
  standalone: true,
  imports: [],
  templateUrl: './location-picker.html',
  styleUrl: './location-picker.css'
})
export class LocationPickerComponent implements AfterViewInit, OnDestroy {
  @Input() initialCoords: string | null = null;
  @Output() coordsSelected = new EventEmitter<string>();

  private map: any;
  private marker: any;

  ngAfterViewInit(): void {
    this.initMap();
  }

  ngOnDestroy(): void {
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap(): void {
    // Default center (Mexico) or initial coords
    let center: L.LatLngExpression = [23.6345, -102.5528];
    let initialZoom = 5;

    if (this.initialCoords) {
      const [lat, lng] = this.parseCoords(this.initialCoords);
      if (lat && lng) {
        center = [lat, lng];
        initialZoom = 15;
      }
    }

    this.map = L.map('picker-map', {
      center: center,
      zoom: initialZoom
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(this.map);

    // If we have initial coords, place marker
    if (this.initialCoords) {
      const [lat, lng] = this.parseCoords(this.initialCoords);
      if (lat && lng) {
        this.placeMarker(lat, lng);
      }
    }

    // Map Click Event
    this.map.on('click', (e: any) => {
      this.placeMarker(e.latlng.lat, e.latlng.lng);
      this.emitCoords(e.latlng.lat, e.latlng.lng);
    });
  }

  private placeMarker(lat: number, lng: number): void {
    if (this.marker) {
      this.marker.setLatLng([lat, lng]);
    } else {
      this.marker = L.marker([lat, lng], { draggable: true }).addTo(this.map);
      this.marker.on('dragend', (event: any) => {
        const position = event.target.getLatLng();
        this.emitCoords(position.lat, position.lng);
      });
    }
    this.map.panTo([lat, lng]);
  }

  private emitCoords(lat: number, lng: number): void {
    // Format to 5 decimal places for precision without excess
    const coordString = `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
    this.coordsSelected.emit(coordString);
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
}
