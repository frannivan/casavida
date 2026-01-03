import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/fraccionamientos/';

@Injectable({
    providedIn: 'root'
})
export class FraccionamientoService {
    constructor(private http: HttpClient) { }

    getAllFraccionamientos(): Observable<any> {
        return this.http.get(API_URL + 'public');
    }
}
