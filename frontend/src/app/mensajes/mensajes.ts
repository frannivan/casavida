import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-mensajes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './mensajes.html'
})
export class MensajesComponent {}
