import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { StorageService } from '../services/storage';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const storageService = inject(StorageService); // Inject StorageService
    const user = storageService.getUser();
    const token = user ? user.accessToken || user.token : null; // Check both potential fields

    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req);
};
