import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/ventas/';

export interface CotizacionRequest {
  montoTotal: number;
  enganche: number;
  plazoMeses: number;
  tasaAnual: number;
}

export interface ContratoRequest {
  clienteId: number;
  loteId: number;
  montoTotal: number;
  enganche: number;
  plazoMeses: number;
  tasaAnual: number;
}

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  constructor(private http: HttpClient) { }

  cotizar(request: CotizacionRequest): Observable<any> {
    return this.http.post(API_URL + 'cotizar', request);
  }

  crearContrato(contrato: ContratoRequest): Observable<any> {
    return this.http.post(API_URL + 'contratar', contrato);
  }

  getAllContratos(): Observable<any[]> {
    return this.http.get<any[]>(API_URL + 'contratos');
  }

  downloadEstadoCuenta(contratoId: number): Observable<Blob> {
    return this.http.get('/api/reportes/estado-cuenta/' + contratoId, { responseType: 'blob' });
  }
}
