import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/reportes/';

export interface DashboardStats {
    totalLotes: number;
    lotesDisponibles: number;
    lotesVendidos: number;
    totalClientes: number;
    totalContratos: number;
}

@Injectable({
    providedIn: 'root'
})
export class ReporteService {
    constructor(private http: HttpClient) { }

    getDashboardStats(): Observable<DashboardStats> {
        return this.http.get<DashboardStats>(API_URL + 'dashboard');
    }
}
