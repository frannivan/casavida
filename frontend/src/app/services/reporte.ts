import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/reportes/';
const UPLOAD_URL = environment.apiUrl + '/upload/';

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private http = inject(HttpClient);

  constructor() { }

  // Dashboard stats
  getDashboardStats(): Observable<any> {
    return this.http.get(API_URL + 'dashboard');
  }

  // Reportes con filtros
  getLotesReport(filtros?: any): Observable<any> {
    let params = new HttpParams();
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
      if (filtros.clienteId) params = params.set('clienteId', filtros.clienteId);
      if (filtros.fraccionamientoId) params = params.set('fraccionamientoId', filtros.fraccionamientoId);
      if (filtros.estatus) params = params.set('estatus', filtros.estatus);
    }
    return this.http.get(API_URL + 'inventario', { params });
  }

  getClientesReport(filtros?: any): Observable<any> {
    let params = new HttpParams();
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
    }
    return this.http.get(API_URL + 'usuarios', { params });
  }

  getContratosReport(filtros?: any): Observable<any> {
    let params = new HttpParams();
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
      if (filtros.vendedorId) params = params.set('vendedorId', filtros.vendedorId);
    }
    return this.http.get(API_URL + 'contratos', { params });
  }

  getPagosReport(filtros?: any): Observable<any> {
    let params = new HttpParams();
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
      if (filtros.clienteId) params = params.set('clienteId', filtros.clienteId);
    }
    return this.http.get(API_URL + 'pagos', { params });
  }

  // Exportar a Excel
  exportarExcel(tipo: string, filtros?: any): Observable<Blob> {
    let params = new HttpParams().set('format', 'excel');
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
    }
    
    const endpoint = tipo === 'clientes' ? 'usuarios' : 
                     tipo === 'lotes' ? 'inventario' : tipo;
    
    return this.http.get(API_URL + endpoint, { 
      params, 
      responseType: 'blob' 
    });
  }

  // Exportar a PDF
  exportarPDF(tipo: string, filtros?: any): Observable<Blob> {
    let params = new HttpParams().set('format', 'pdf');
    if (filtros) {
      if (filtros.fechaDesde) params = params.set('startDate', filtros.fechaDesde);
      if (filtros.fechaHasta) params = params.set('endDate', filtros.fechaHasta);
    }
    
    const endpoint = tipo === 'clientes' ? 'usuarios' : 
                     tipo === 'lotes' ? 'inventario' : tipo;
    
    return this.http.get(API_URL + endpoint, { 
      params, 
      responseType: 'blob' 
    });
  }

  // Descargar estado de cuenta
  downloadEstadoCuenta(contratoId: number): Observable<Blob> {
    return this.http.get(API_URL + 'estado-cuenta/' + contratoId, { 
      responseType: 'blob' 
    });
  }
}

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private http = inject(HttpClient);

  constructor() { }

  // Subir archivo Excel
  uploadFile(tipo: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(UPLOAD_URL + tipo, formData);
  }

  // Descargar plantilla
  downloadTemplate(tipo: string): Observable<Blob> {
    return this.http.get(UPLOAD_URL + 'template/' + tipo, {
      responseType: 'blob'
    });
  }
}
