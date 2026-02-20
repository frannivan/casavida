import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { StorageService } from '../../services/storage';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  roles: string[] = [];
  isAdmin = false;
  isRecepcion = false;
  isVendedor = false;
  isContabilidad = false;
  isDirectivo = false;
  isUser = false;

  private storageService = inject(StorageService);
  private authService = inject(AuthService);
  private router = inject(Router);

  constructor() {
    const user = this.storageService.getUser();
    this.roles = user?.roles || [];
    this.isAdmin = this.roles.includes('ROLE_ADMIN');
    this.isRecepcion = this.roles.includes('ROLE_RECEPCION');
    this.isVendedor = this.roles.includes('ROLE_VENDEDOR');
    this.isContabilidad = this.roles.includes('ROLE_CONTABILIDAD');
    this.isDirectivo = this.roles.includes('ROLE_DIRECTIVO');
    this.isUser = this.roles.includes('ROLE_USER');
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.storageService.clean();
        this.router.navigate(['/login']).then(() => {
          window.location.reload();
        });
      },
      error: () => {
        this.storageService.clean();
        this.router.navigate(['/login']).then(() => {
          window.location.reload();
        });
      }
    });
  }
}
