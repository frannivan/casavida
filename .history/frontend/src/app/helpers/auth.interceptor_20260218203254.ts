import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage';

import { HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const storageService = inject(StorageService); // Inject StorageService
    const router = inject(Router);
    const user = storageService.getUser();
    const token = user ? user.accessToken || user.token : null; // Check both potential fields

    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 401) {
                // Auto logout if 401 response returned from API
                storageService.clean();
                localStorage.clear(); // Ensure clean slate
                sessionStorage.clear();
                
                // Only redirect if not already on login page to avoid loops
                if (!router.url.includes('/login')) {
                    router.navigate(['/login']);
                }
            }
            return throwError(() => error);
        })
    );
};
