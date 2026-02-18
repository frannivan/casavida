import { Component, inject, OnInit } from '@angular/core';
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
export class SidebarComponent implements OnInit {
  roles: string[] = [];
  isAdmin = false;
  isRecepcion = false;
  isVendedor = false;
  isContabilidad = false;
  isDirectivo = false;
  isUser = false;

  private storageService = inject(StorageService);
  private router = inject(Router);

  ngOnInit(): void {
    this.loadUserRoles();
  }

  loadUserRoles(): void {
    const user = this.storageService.getUser();
    
    // Debug: ver qué llega del storage
    console.log('[Sidebar] User from storage:', user);
    console.log('[Sidebar] Roles:', user?.roles);
    
    this.roles = user?.roles || [];
    
    this.isAdmin = this.roles.includes('ROLE_ADMIN');
    this.isRecepcion = this.roles.includes('ROLE_RECEPCION');
    this.isVendedor = this.roles.includes('ROLE_VENDEDOR');
    this.isContabilidad = this.roles.includes('ROLE_CONTABILIDAD');
    this.isDirectivo = this.roles.includes('ROLE_DIRECTIVO');
    this.isUser = this.roles.includes('ROLE_USER');
    
    // Debug: ver qué quedó asignado
    console.log('[Sidebar] Role flags:', {
      isAdmin: this.isAdmin,
      isRecepcion: this.isRecepcion,
      isVendedor: this.isVendedor,
      isContabilidad: this.isContabilidad,
      isDirectivo: this.isDirectivo,
      isUser: this.isUser
    });
  }

  logout(): void {
    this.storageService.clean();
    localStorage.clear();
    sessionStorage.clear();
    document.cookie.split(";").forEach(function(c) {
      document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
    });
    window.location.href = '/casavida/home';
  }
}
