import { Component, OnInit } from '@angular/core';
import { TicketService, Ticket } from '../../services/ticket.service';
import { StorageService } from '../../services/storage';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-soporte',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './soporte.component.html',
  styleUrls: ['./soporte.component.css']
})
export class SoporteComponent implements OnInit {
  tickets: Ticket[] = [];
  isAdmin = false;
  isSoporte = false;
  
  // Form fields
  newTicket: any = {
    titulo: '',
    descripcion: '',
    prioridad: 'MEDIA'
  };
  selectedFile: File | null = null;
  
  // Management fields
  selectedTicket: Ticket | null = null;
  newComment = '';
  
  loading = false;
  message = '';

  constructor(
    private ticketService: TicketService,
    private storageService: StorageService
  ) { }

  ngOnInit(): void {
    const roles = this.storageService.getUser().roles;
    this.isAdmin = roles.includes('ROLE_ADMIN');
    this.isSoporte = roles.includes('ROLE_SOPORTE');
    
    this.loadTickets();
  }

  loadTickets(): void {
    this.loading = true;
    const request = this.isAdmin && !this.isSoporte 
      ? this.ticketService.getMyTickets() 
      : this.ticketService.getAllTickets();
      
    request.subscribe({
      next: data => {
        this.tickets = data;
        this.loading = false;
      },
      error: err => {
        console.error(err);
        this.loading = false;
      }
    });
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
  }

  onSubmit(): void {
    if (!this.newTicket.titulo || !this.newTicket.descripcion) return;
    
    const formData = new FormData();
    formData.append('titulo', this.newTicket.titulo);
    formData.append('descripcion', this.newTicket.descripcion);
    formData.append('prioridad', this.newTicket.prioridad);
    if (this.selectedFile) {
      formData.append('file', this.selectedFile);
    }

    this.loading = true;
    this.ticketService.createTicket(formData).subscribe({
      next: res => {
        this.message = 'Ticket enviado correctamente.';
        this.newTicket = { titulo: '', descripcion: '', prioridad: 'MEDIA' };
        this.selectedFile = null;
        this.loadTickets();
      },
      error: err => {
        this.message = 'Error al enviar el ticket.';
        this.loading = false;
      }
    });
  }

  selectTicket(ticket: Ticket): void {
    this.selectedTicket = ticket;
    this.newComment = '';
  }

  updateStatus(status: string): void {
    if (!this.selectedTicket?.id) return;
    
    this.ticketService.updateStatus(this.selectedTicket.id, status).subscribe({
      next: res => {
        this.selectedTicket!.estatus = status as any;
        this.loadTickets();
      },
      error: err => console.error(err)
    });
  }

  addComment(): void {
    if (!this.selectedTicket?.id || !this.newComment) return;
    
    this.ticketService.addComment(this.selectedTicket.id, this.newComment).subscribe({
      next: res => {
        // Refresh local ticket data
        this.loadTickets();
        this.newComment = '';
      },
      error: err => console.error(err)
    });
  }

  getEvidenciaUrl(id: number): string {
    return this.ticketService.getEvidenciaUrl(id);
  }
}
