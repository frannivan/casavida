import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StorageService } from './services/storage';
import { AuthService } from './services/auth';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MensajeService } from './services/mensaje';

/**
 * Componente principal de la aplicación que gestiona la estructura de layout,
 * la autenticación global y las notificaciones de mensajes.
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule, ChatbotComponent, SidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  // Roles y estado de sesión
  roles: string[] = [];
  isLoggedIn = false;
  
  // Flags para mostrar paneles específicos por rol
  showAdminBoard = false;
  showModeratorBoard = false;
  showRecepcionBoard = false;
  showVendedorBoard = false;
  showUserBoard = false;
  showContabilidadBoard = false;
  showDirectivoBoard = false;
  
  username?: string;
  unreadMessagesCount = 0; // Contador global de mensajes no leídos

  private storageService = inject(StorageService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private mensajeService = inject(MensajeService);

  constructor() { }

  ngOnInit(): void {
    // Escucha cambios de ruta para limpiar residuos de modales (Bootstrap backdrops)
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());
        document.body.classList.remove('modal-open');
        document.body.style.removeProperty('padding-right');
      }
    });

    // Verifica si el usuario tiene una sesión activa
    this.isLoggedIn = this.storageService.isLoggedIn();

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.username = user.username;
      this.roles = user.roles || [];

      // Mapeo dinámico de visibilidad según roles
      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showRecepcionBoard = this.roles.includes('ROLE_RECEPCION');
      this.showVendedorBoard = this.roles.includes('ROLE_VENDEDOR');
      this.showUserBoard = this.roles.includes('ROLE_USER');
      this.showContabilidadBoard = this.roles.includes('ROLE_CONTABILIDAD');
      this.showDirectivoBoard = this.roles.includes('ROLE_DIRECTIVO');

      // Carga inicial de notificaciones
      this.loadUnreadCount();
    }
  }

  /**
   * Consulta al backend el número de mensajes sin leer del usuario.
   */
  loadUnreadCount(): void {
    if (this.isLoggedIn) {
      this.mensajeService.getUnreadCount().subscribe({
        next: count => this.unreadMessagesCount = count,
        error: err => console.error('Error fetching unread count', err)
      });
    }
  }

  /**
   * Retorna una etiqueta legible para el rol principal del usuario.
   */
  get userRoleDisplay(): string {
    if (this.showAdminBoard) return 'Administrador';
    if (this.showRecepcionBoard) return 'Recepción';
    if (this.showVendedorBoard) return 'Vendedor';
    if (this.showContabilidadBoard) return 'Contabilidad';
    if (this.showDirectivoBoard) return 'Directivo';
    if (this.showUserBoard) return 'Cliente';
    return 'Usuario';
  }

  /**
   * Finaliza la sesión del usuario y limpia el estado local.
   * Redirección absoluta para evitar problemas con Nginx y dominios externos.
   */
  logout(): void {
    this.authService.logout().subscribe({
      next: res => {
        this.storageService.clean();
        window.location.href = '/casavida/login';
      },
      error: err => {
        console.log(err);
        this.storageService.clean();
        window.location.href = '/casavida/login';
      }
    });
  }
}
