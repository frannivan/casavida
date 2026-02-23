import { Component, OnInit, inject } from '@angular/core';
import { StorageService } from '../services/storage';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { VentaService } from '../services/venta';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {
  currentUser: any;
  contratos: any[] = [];
  
  // Password Change
  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };
  passwordMessage = '';
  passwordError = '';
  isPasswordChanging = false;

  private ventaService = inject(VentaService);
  private userService = inject(UserService);

  constructor(private storageService: StorageService) { }

  ngOnInit(): void {
    this.currentUser = this.storageService.getUser();
    if (this.currentUser) {
      this.loadContratos();
    }
  }

  loadContratos(): void {
    this.ventaService.getMisContratos().subscribe({
      next: data => {
        this.contratos = data;
      },
      error: err => console.error(err)
    });
  }

  onChangePassword(): void {
    this.passwordMessage = '';
    this.passwordError = '';

    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.passwordError = 'Las contraseñas nuevas no coinciden.';
      return;
    }

    this.isPasswordChanging = true;
    this.userService.changePassword({
      currentPassword: this.passwordForm.currentPassword,
      newPassword: this.passwordForm.newPassword
    }).subscribe({
      next: res => {
        this.passwordMessage = res.message;
        this.isPasswordChanging = false;
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
      },
      error: err => {
        this.passwordError = err.error.message || 'Error al cambiar contraseña';
        this.isPasswordChanging = false;
      }
    });
  }

  get userRoleDisplay(): string {
    const role = this.currentUser?.role || '';
    if (role === 'ROLE_ADMIN') return 'Administrador';
    if (role === 'ROLE_VENDEDOR') return 'Vendedor';
    if (role === 'ROLE_RECEPCION') return 'Recepción';
    return 'Cliente';
  }
}
