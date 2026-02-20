import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { StorageService } from '../../services/storage';
import { AuthService } from '../../services/auth';

/**
 * Componente de barra lateral (Sidebar) que gestiona el menú de navegación principal.
 * Filtra las opciones visibles según los roles del usuario autenticado.
 */
@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  // Almacena los roles del usuario actual
  roles: string[] = [];
  
  // Estados booleanos para simplificar las condiciones en el HTML
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
    // Inicializa los estados de roles al cargar el componente
    const user = this.storageService.getUser();
    this.roles = user?.roles || [];
    this.isAdmin = this.roles.includes('ROLE_ADMIN');
    this.isRecepcion = this.roles.includes('ROLE_RECEPCION');
    this.isVendedor = this.roles.includes('ROLE_VENDEDOR');
    this.isContabilidad = this.roles.includes('ROLE_CONTABILIDAD');
    this.isDirectivo = this.roles.includes('ROLE_DIRECTIVO');
    this.isUser = this.roles.includes('ROLE_USER');
  }

  /**
   * Cierra la sesión del usuario, limpia el almacenamiento local y redirige al login.
   * Se usa window.location.href con el prefijo /casavida/ para asegurar que no se escape
   * del contexto de la aplicación hacia la raíz del servidor.
   */
  logout(): void {
    this.authService.logout().subscribe({
      next: () => {
        this.storageService.clean();
        window.location.href = '/casavida/login';
      },
      error: () => {
        this.storageService.clean();
        window.location.href = '/casavida/login';
      }
    });
  }
}
