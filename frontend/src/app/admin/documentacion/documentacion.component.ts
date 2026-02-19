import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { StorageService } from '../../services/storage';

@Component({
  selector: 'app-documentacion',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './documentacion.component.html',
  styleUrls: ['./documentacion.component.css']
})
export class DocumentacionComponent implements OnInit {
  docList: string[] = [];
  selectedDoc: string | null = null;
  docContent: SafeHtml = '';
  loading = false;
  
  private http = inject(HttpClient);
  private sanitizer = inject(DomSanitizer);
  private storageService = inject(StorageService);
  private apiUrl = environment.apiUrl;

  ngOnInit(): void {
    this.loadDocList();
  }

  loadDocList(): void {
    const user = this.storageService.getUser();
    const roles = user ? user.roles : [];
    const isAdmin = roles.includes('ROLE_ADMIN');
    const isVendedor = roles.includes('ROLE_VENDEDOR');
    const isRecepcion = roles.includes('ROLE_RECEPCION');

    this.http.get<string[]>(`${this.apiUrl}/docs/list`).subscribe({
      next: (data: string[]) => {
        if (isAdmin) {
            this.docList = data;
        } else {
            this.docList = data.filter((doc: string) => {
                const lowerDoc = doc.toLowerCase();
                if (isVendedor) {
                    return lowerDoc.includes('vendedor') || lowerDoc.includes('general') || lowerDoc.includes('ventas');
                }
                if (isRecepcion) {
                    return lowerDoc.includes('recepcion') || lowerDoc.includes('general') || lowerDoc.includes('caja');
                }
                return false;
            });
        }

        if (this.docList.length > 0) {
          this.viewDoc(this.docList[0]); // Auto-select first doc
        } else if (!isAdmin) {
             // Fallback if no specific doc found
             this.docContent = '<h3>No hay manuales asignados para tu rol.</h3>';
        }
      },
      error: (err: any) => console.error('Error loading doc list', err)
    });
  }

  viewDoc(filename: string): void {
    this.selectedDoc = filename;
    this.loading = true;
    this.http.get(`${this.apiUrl}/docs/${filename}`, { responseType: 'text' }).subscribe({
      next: (markdown: string) => {
        this.docContent = this.sanitizer.sanitize(1, this.parseMarkdown(markdown)) || '';
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error loading doc', err);
        this.loading = false;
      }
    });
  }

  parseMarkdown(md: string): string {
    if (!md) return '';
    
    let html = md
      // Headers
      .replace(/^### (.*$)/gim, '<h3>$1</h3>')
      .replace(/^## (.*$)/gim, '<h2>$1</h2>')
      .replace(/^# (.*$)/gim, '<h1>$1</h1>')
      // Bold
      .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
      // Lists
      .replace(/^\* (.*$)/gim, '<li>$1</li>')
      .replace(/^- (.*$)/gim, '<li>$1</li>')
      // Line breaks
      .replace(/\n/gim, '<br>');
    
    // Wrap consecutive <li> in <ul>
    html = html.replace(/(<li>.*?<\/li>(<br>)?)+/gim, '<ul>$&</ul>');
    
    return html;
  }

  printDoc(): void {
    window.print();
  }
}
