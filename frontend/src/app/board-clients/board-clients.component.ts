import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../services/cliente';

@Component({
    selector: 'app-board-clients',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './board-clients.component.html',
    styleUrl: './board-clients.component.css'
})
export class BoardClientsComponent implements OnInit {
    clientes: any[] = [];
    showClientModal = false;
    isEditingClient = false;
    editingClientId: number | null = null;
    clientData = {
        nombre: '',
        apellidos: '',
        email: '',
        telefono: '',
        direccion: '',
        ine: ''
    };
    clientSuccessMsg = '';
    clientErrorMsg = '';

    constructor(private clienteService: ClienteService) { }

    ngOnInit(): void {
        this.loadClientes();
    }

    loadClientes(): void {
        this.clienteService.getAllClientes().subscribe({
            next: data => this.clientes = data,
            error: err => console.error('Error loading clients', err)
        });
    }

    openClientModal(clientToEdit: any = null): void {
        this.showClientModal = true;
        this.clientSuccessMsg = '';
        this.clientErrorMsg = '';

        if (clientToEdit) {
            this.isEditingClient = true;
            this.editingClientId = clientToEdit.id;
            this.clientData = { ...clientToEdit };
        } else {
            this.isEditingClient = false;
            this.editingClientId = null;
            this.clientData = {
                nombre: '',
                apellidos: '',
                email: '',
                telefono: '',
                direccion: '',
                ine: ''
            };
        }
    }

    submitClient(): void {
        if (this.isEditingClient && this.editingClientId) {
            this.clienteService.updateCliente(this.editingClientId, this.clientData).subscribe({
                next: res => {
                    this.clientSuccessMsg = 'Cliente actualizado exitosamente.';
                    this.loadClientes();
                    setTimeout(() => this.showClientModal = false, 2000);
                },
                error: err => {
                    this.clientErrorMsg = err.error?.message || 'Error al actualizar cliente.';
                }
            });
        } else {
            this.clienteService.createCliente(this.clientData).subscribe({
                next: res => {
                    this.clientSuccessMsg = 'Cliente registrado exitosamente.';
                    this.loadClientes();
                    setTimeout(() => this.showClientModal = false, 2000);
                },
                error: err => {
                    this.clientErrorMsg = err.error?.message || 'Error al registrar cliente.';
                }
            });
        }
    }

    deleteClient(id: number): void {
        // Optional: Implement delete if needed later
        if (confirm('¿Estás seguro de eliminar este cliente?')) {
            // Call service delete
        }
    }
}
