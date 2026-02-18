import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet, RouterLink, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StorageService } from './services/storage';
import { AuthService } from './services/auth';
import { ChatbotComponent } from './components/chatbot/chatbot.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';

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
  username?: string;

  private storageService = inject(StorageService);
  private authService = inject(AuthService);
  private router = inject(Router);

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

      console.log('Current User:', user);

      // DEBUG: Remove in production
      // alert('DEBUG: User loaded: ' + JSON.stringify(user));

      this.username = user.username;
      this.roles = user.roles || [];

      this.showAdminBoard = this.roles.includes('ROLE_ADMIN');
      this.showRecepcionBoard = this.roles.includes('ROLE_RECEPCION');
      this.showVendedorBoard = this.roles.includes('ROLE_VENDEDOR');
      this.showUserBoard = this.roles.includes('ROLE_USER');
    }
  }

  get userRoleDisplay(): string {
    if (this.showAdminBoard) return 'Administrador';
    if (this.showRecepcionBoard) return 'Recepción';
    if (this.showVendedorBoard) return 'Vendedor';
    if (this.showUserBoard) return 'Cliente';
    return 'Usuario';
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: res => {
        this.storageService.clean();
        window.location.href = '/casavida/home'; // Redirect to Home
      },
      error: err => {
        console.log(err);
        this.storageService.clean();
        window.location.href = '/casavida/home'; // Redirect to Home
      }
    });
  }
}
