import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/lotes/';

@Injectable({
  providedIn: 'root'
})
export class LoteService {

  constructor(private http: HttpClient) { }

  getPublicLotes(fraccionamientoId?: any, ubicacion?: string, sortDir: string = 'asc'): Observable<any> {
    let params: any = { sortDir };
    if (fraccionamientoId) params['fraccionamientoId'] = fraccionamientoId;
    if (ubicacion) params['ubicacion'] = ubicacion;

    return this.http.get(API_URL + 'public', { params });
  }

  getLoteById(id: number): Observable<any> {
    return this.http.get(API_URL + 'public/' + id);
  }

  getAllLotes(): Observable<any> {
    return this.http.get(API_URL + 'all');
  }

  createLote(lote: any): Observable<any> {
    return this.http.post(API_URL + 'create', lote);
  }

  updateLote(id: number, lote: any): Observable<any> {
    return this.http.put(API_URL + id, lote);
  }

  deleteLote(id: number): Observable<any> {
    return this.http.delete(API_URL + id);
  }
}
