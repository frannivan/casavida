import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = '/api/fraccionamientos/';

@Injectable({
    providedIn: 'root'
})
export class FraccionamientoService {
    constructor(private http: HttpClient) { }

    getAllFraccionamientos(): Observable<any> {
        return this.http.get(API_URL + 'public');
    }
}
