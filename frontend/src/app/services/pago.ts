import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_URL = '/api/pagos/';

export interface Pago {
    id: number;
    fechaPago: string;
    monto: number;
    referencia: string;
    concepto: string;
}

@Injectable({
    providedIn: 'root'
})
export class PagoService {
    constructor(private http: HttpClient) { }

    getPagosByContrato(contratoId: number): Observable<Pago[]> {
        return this.http.get<Pago[]>(API_URL + 'contrato/' + contratoId);
    }

    registrarPago(pago: any): Observable<any> {
        return this.http.post(API_URL + 'registrar', pago);
    }
}
