import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const API_URL = environment.apiUrl + '/mensajes/';

export interface Mensaje {
  id: number;
  remitente: { id: number, username: string };
  destinatario: { id: number, username: string };
  asunto: string;
  contenido: string;
  fechaEnvio: string;
  leido: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class MensajeService {
  private http = inject(HttpClient);

  getRecibidos(): Observable<Mensaje[]> {
    return this.http.get<Mensaje[]>(API_URL + 'recibidos');
  }

  getEnviados(): Observable<Mensaje[]> {
    return this.http.get<Mensaje[]>(API_URL + 'enviados');
  }

  enviarMensaje(asunto: string, contenido: string, destinatarioId: number): Observable<any> {
    return this.http.post(API_URL + 'enviar', { asunto, contenido, destinatarioId });
  }

  marcarComoLeido(id: number): Observable<any> {
    return this.http.put(API_URL + id + '/leer', {});
  }

  getUnreadCount(): Observable<number> {
    return this.http.get<number>(API_URL + 'unread-count');
  }
}
