import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, Router } from '@angular/router';
import { StorageService } from '../../services/storage';

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

  private storageService = inject(StorageService);
  private router = inject(Router);

  constructor() {
    const user = this.storageService.getUser();
    this.roles = user?.roles || [];
    this.isAdmin = this.roles.includes('ROLE_ADMIN');
    this.isRecepcion = this.roles.includes('ROLE_RECEPCION');
    this.isVendedor = this.roles.includes('ROLE_VENDEDOR');
    this.isContabilidad = this.roles.includes('ROLE_CONTABILIDAD');
    this.isDirectivo = this.roles.includes('ROLE_DIRECTIVO');
  }

  logout(): void {
    this.storageService.clean();
    // Also clear localStorage just in case, as per previous implementation
    localStorage.clear();
    sessionStorage.clear();
    document.cookie.split(";").forEach(function(c) {
      document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
    });
    // Force reload to clear all states
    window.location.href = '/login';
  }
}
