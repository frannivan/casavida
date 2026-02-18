import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const ADMIN_API = environment.apiUrl + '/admin/';

const httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
    providedIn: 'root'
})
export class AdminService {

    private http = inject(HttpClient);

    constructor() { }

    getUsers(): Observable<any> {
        return this.http.get(ADMIN_API + 'users');
    }

    createUser(user: any): Observable<any> {
        return this.http.post(ADMIN_API + 'users', user, httpOptions);
    }

    updateUser(id: number, user: any): Observable<any> {
        return this.http.put(ADMIN_API + 'users/' + id, user, httpOptions);
    }

    deleteUser(id: number): Observable<any> {
        return this.http.delete(ADMIN_API + 'users/' + id);
    }
}
