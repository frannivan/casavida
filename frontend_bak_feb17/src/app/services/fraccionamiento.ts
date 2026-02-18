import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/fraccionamientos/';

@Injectable({
    providedIn: 'root'
})
export class FraccionamientoService {
    private http = inject(HttpClient);

    constructor() { }

    getAllFraccionamientos(): Observable<any> {
        return this.http.get(API_URL + 'public');
    }

    getById(id: number): Observable<any> {
        return this.http.get(API_URL + 'public/' + id);
    }

    createFraccionamiento(fraccionamiento: any): Observable<any> {
        return this.http.post(API_URL + 'create', fraccionamiento);
    }

    updateFraccionamiento(id: number, data: any): Observable<any> {
        return this.http.put(API_URL + id, data);
    }

    deleteFraccionamiento(id: number): Observable<any> {
        return this.http.delete(API_URL + id);
    }
}
