import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StorageService } from './services/storage';
import { AuthService } from './services/auth';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MensajeService } from './services/mensaje';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule, ChatbotComponent, SidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  roles: string[] = [];
  isLoggedIn = false;
  showAdminBoard = false;
  showModeratorBoard = false;
  showRecepcionBoard = false;
  showVendedorBoard = false;
  showUserBoard = false;
  showContabilidadBoard = false;
  showDirectivoBoard = false;
  username?: string;
  unreadMessagesCount = 0;

  private storageService = inject(StorageService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private mensajeService = inject(MensajeService);

  constructor() { }

  ngOnInit(): void {
    // Cleanup modal backdrops on route change
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        const backdrops = document.querySelectorAll('.modal-backdrop');
        backdrops.forEach(backdrop => backdrop.remove());
        document.body.classList.remove('modal-open');
        document.body.style.removeProperty('padding-right');
      }
    });

    this.isLoggedIn = this.storageService.isLoggedIn();

    if (this.isLoggedIn) {
      const user = this.storageService.getUser();
      this.roles = user.roles;

      console.log('Login successful. User:', user.username);
      console.log('User Roles:', user.roles);
      // alert('PERMISOS RECIBIDOS: ' + JSON.stringify(user.roles));
      
      this.username = user.username;
      this.roles = user.roles || [];

      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showRecepcionBoard = this.roles.includes('ROLE_RECEPCION');
      this.showVendedorBoard = this.roles.includes('ROLE_VENDEDOR');
      this.showUserBoard = this.roles.includes('ROLE_USER');
      this.showContabilidadBoard = this.roles.includes('ROLE_CONTABILIDAD');
      this.showDirectivoBoard = this.roles.includes('ROLE_DIRECTIVO');

      this.loadUnreadCount();
    }
  }

  loadUnreadCount(): void {
    if (this.isLoggedIn) {
      this.mensajeService.getUnreadCount().subscribe({
        next: count => this.unreadMessagesCount = count,
        error: err => console.error('Error fetching unread count', err)
      });
    }
  }

  get userRoleDisplay(): string {
    if (this.showAdminBoard) return 'Administrador';
    if (this.showRecepcionBoard) return 'Recepción';
    if (this.showVendedorBoard) return 'Vendedor';
    if (this.showContabilidadBoard) return 'Contabilidad';
    if (this.showDirectivoBoard) return 'Directivo';
    if (this.showUserBoard) return 'Cliente';
    return 'Usuario';
  }

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
