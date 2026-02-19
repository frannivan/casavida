import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../services/admin';

@Component({
    selector: 'app-admin-users',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './admin-users.component.html'
})
export class AdminUsersComponent implements OnInit {
    users: any[] = [];
    filteredUsers: any[] = [];
    searchTerm = '';
    roleFilter = '';
    showModal = false;
    isEditing = false;

    currentUser: any = {
        username: '',
        email: '',
        password: '',
        role: ''  // Default empty, per request
    };

    roles = ['ADMIN', 'USER', 'VENDEDOR', 'RECEPCION'];

    private adminService = inject(AdminService);
    private cdr = inject(ChangeDetectorRef);

    constructor() { }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.adminService.getUsers().subscribe({
            next: (data) => {
                this.users = data;
                this.filteredUsers = data;
                this.cdr.detectChanges();
            },
            error: (err) => console.error(err)
        });
    }

    filterUsers(): void {
        this.filteredUsers = this.users.filter(user => {
            const matchesSearch = !this.searchTerm || 
                (user.username?.toLowerCase().includes(this.searchTerm.toLowerCase()) || false) ||
                (user.email?.toLowerCase().includes(this.searchTerm.toLowerCase()) || false);
            
            const matchesRole = !this.roleFilter || 
                user.role === this.roleFilter;
            
            return matchesSearch && matchesRole;
        });
    }

    clearFilters(): void {
        this.searchTerm = '';
        this.roleFilter = '';
        this.filteredUsers = this.users;
    }


    openCreateModal(): void {
        this.isEditing = false;
        this.currentUser = { username: '', email: '', password: '', role: '' };
        this.showModal = true;
    }

    openEditModal(user: any): void {
        this.isEditing = true;
        this.currentUser = { ...user, password: '' };
        this.showModal = true;
    }

    closeModal(): void {
        this.showModal = false;
    }

    saveUser(): void {
        if (this.isEditing) {
            this.adminService.updateUser(this.currentUser.id, this.currentUser).subscribe({
                next: () => {
                    this.loadUsers();
                    this.closeModal();
                },
                error: (err) => alert('Error actualizando: ' + (err.error?.message || err.message))
            });
        } else {
            this.adminService.createUser(this.currentUser).subscribe({
                next: () => {
                    this.loadUsers();
                    this.closeModal();
                },
                error: (err) => alert('Error creando: ' + (err.error?.message || err.message))
            });
        }
    }

    deleteUser(id: number): void {
        if (confirm('¿Eliminar usuario?')) {
            this.adminService.deleteUser(id).subscribe({
                next: () => this.loadUsers(),
                error: (err) => alert('Error eliminando: ' + (err.error?.message || err.message))
            });
        }
    }
}
