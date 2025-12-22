import { Component, OnInit } from '@angular/core';
import { LoteService } from '../services/lote';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocationPickerComponent } from '../components/location-picker/location-picker';
import { FraccionamientoService } from '../services/fraccionamiento';
import { ReporteService } from '../services/reporte';
import { VentaService } from '../services/venta';
import { ClienteService } from '../services/cliente';
import { PagoService } from '../services/pago';

import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-board-admin',
    standalone: true,
    imports: [CommonModule, FormsModule, LocationPickerComponent, RouterLink],
    templateUrl: './board-admin.component.html',
    styleUrl: './board-admin.component.css'
})
export class BoardAdminComponent implements OnInit {
    content?: string;
    lotes: any[] = [];
    fraccionamientos: any[] = [];
    newLote: any = {
        numeroLote: '',
        manzana: '',
        precioTotal: 0,
        areaMetrosCuadrados: 0,
        coordenadasGeo: '',
        fraccionamiento: null,
        estatus: 'DISPONIBLE'
    };
    isCreating = false;
    stats: any = null;

    // Sales & Payments Modules
    showContractModal = false;
    showPaymentModal = false;


    // Data Loading
    clientes: any[] = [];
    contratosCliente: any[] = [];

    // Contract Form

    // Contract Form
    contractData: any = {
        clienteId: null,
        loteId: null,
        montoTotal: 0,
        enganche: 0,
        plazoMeses: 12,
        tasaAnual: 10
    };
    loteSelectedForContract: any = null;

    // Payment Form
    paymentData = {
        clienteId: null,
        contratoId: null,
        monto: 0,
        referencia: '',
        concepto: 'Mensualidad'
    };
    pagosActuales: any[] = []; // History List
    paymentSuccessMsg = '';
    paymentErrorMsg = '';
    contractSuccessMsg = '';
    contractErrorMsg = '';

    // History Logic
    showHistoryModal = false;
    historyContract: any = null;

    constructor(
        private loteService: LoteService,
        private fraccionamientoService: FraccionamientoService,
        private reporteService: ReporteService,
        private venteService: VentaService,
        private clienteService: ClienteService,
        private pagoService: PagoService
    ) { }

    ngOnInit(): void {
        this.reporteService.getDashboardStats().subscribe({
            next: data => {
                this.stats = data;
            },
            error: err => console.error('Error fetching stats', err)
        });

        this.loadLotes();
        this.loadClientes();
        this.loadAllContratos(); // New: Fetch contracts for inventory mapping
    }

    loadStats(): void {
        console.log('Fetching Stats...');
        this.reporteService.getDashboardStats().subscribe({
            next: data => {
                console.log('Stats Loaded:', data);
                this.stats = data;
            },
            error: err => {
                console.error('Error fetching stats', err);
                this.content = "Error loading stats: " + (err.error?.message || err.message);
            }
        });
    }

    loadLotes(): void {
        this.loteService.getAllLotes().subscribe({
            next: data => this.lotes = data,
            error: err => {
                console.error(err);
                if (err.error) {
                    try {
                        this.content = JSON.parse(err.error).message;
                    } catch (e) {
                        this.content = "Error accessing admin content.";
                    }
                }
            }
        });
    }

    loadFraccionamientos(): void {
        this.fraccionamientoService.getAllFraccionamientos().subscribe({
            next: data => this.fraccionamientos = data,
            error: err => console.error(err)
        });
    }

    loadClientes(): void {
        this.clienteService.getAllClientes().subscribe({
            next: data => this.clientes = data,
            error: err => console.error('Error loading clients', err)
        });
    }

    // Contracts Map: LoteId -> Contrato Info
    loteContratoMap: { [key: number]: any } = {};

    loadAllContratos(): void {
        this.venteService.getAllContratos().subscribe({
            next: data => {
                console.log('Todos los contratos:', data); // DEBUG
                if (data && data.length > 0) {
                    data.forEach(c => {
                        if (c.lote) {
                            this.loteContratoMap[c.lote.id] = c;
                        }
                    });
                }
                console.log('Mapa Lote->Contrato:', this.loteContratoMap); // DEBUG
            },
            error: err => console.error(err)
        });
    }

    // --- Contract Logic ---
    openContractModal(): void {
        this.showContractModal = true;
        this.contractSuccessMsg = '';
        this.contractErrorMsg = '';
    }

    onLoteSelectForContract(lote: any): void {
        this.loteSelectedForContract = lote;
        this.contractData.loteId = lote.id;
        this.contractData.montoTotal = lote.precioTotal;
        this.contractData.enganche = lote.precioTotal * 0.10; // Default 10%
    }

    submitContract(): void {
        if (!this.contractData.clienteId || !this.contractData.loteId) {
            this.contractErrorMsg = 'Selecciona Cliente y Lote.';
            return;
        }
        this.venteService.crearContrato(this.contractData).subscribe({
            next: res => {
                this.contractSuccessMsg = res.message;
                this.loadStats(); // Update counters
                this.loadLotes(); // Update lote estatus
                setTimeout(() => this.showContractModal = false, 2000);
            },
            error: err => {
                this.contractErrorMsg = err.error?.message || 'Error al crear contrato.';
            }
        });
    }

    // --- Payment Logic ---
    openPaymentModal(): void {
        this.showPaymentModal = true;
        this.paymentSuccessMsg = '';
        this.paymentErrorMsg = '';
        this.paymentData = {
            clienteId: null,
            contratoId: null,
            monto: 0,
            referencia: '',
            concepto: 'Mensualidad'
        };
        this.contratosCliente = [];
        this.pagosActuales = [];
    }

    onPaymentContractSelect(): void {
        if (this.paymentData.contratoId) {
            this.pagoService.getPagosByContrato(this.paymentData.contratoId).subscribe({
                next: data => this.pagosActuales = data,
                error: err => console.error(err)
            });
        } else {
            this.pagosActuales = [];
        }
    }

    onPaymentClientSelect(): void {
        this.contratosCliente = [];
        this.paymentData.contratoId = null;
        this.pagosActuales = [];

        if (this.paymentData.clienteId) {
            this.clienteService.getContratosByCliente(this.paymentData.clienteId).subscribe({
                next: data => {
                    this.contratosCliente = data;
                },
                error: err => console.error('Error fetching client contracts', err)
            });
        }
    }

    submitPayment(): void {
        // Assuming we have contractId
        // Send flat DTO
        const pago = {
            contratoId: this.paymentData.contratoId,
            fechaPago: new Date().toISOString().split('T')[0], // YYYY-MM-DD
            monto: this.paymentData.monto,
            referencia: this.paymentData.referencia,
            concepto: this.paymentData.concepto
        };

        this.pagoService.registrarPago(pago).subscribe({
            next: res => {
                this.paymentSuccessMsg = 'Pago registrado.';
                setTimeout(() => this.showPaymentModal = false, 2000);
            },
            error: err => this.paymentErrorMsg = 'Error al registrar pago.'
        });
    }




    onCreateLote(): void {
        this.loteService.createLote(this.newLote).subscribe({
            next: data => {
                console.log(data);
                this.isCreating = false;
                this.loadLotes();
                this.newLote = { numeroLote: '', manzana: '', precioTotal: 0, areaMetrosCuadrados: 0, coordenadasGeo: '', fraccionamiento: null, estatus: 'DISPONIBLE' };
            },
            error: err => console.error(err)
        });
    }

    verHistorial(lote: any): void {
        const contrato = this.loteContratoMap[lote.id];
        if (contrato) {
            this.historyContract = contrato;
            this.showHistoryModal = true;

            // Load payments
            this.pagosActuales = [];
            this.pagoService.getPagosByContrato(contrato.id).subscribe({
                error: err => console.error(err)
            });
        }
    }

    downloadReport(): void {
        if (this.historyContract) {
            this.venteService.downloadEstadoCuenta(this.historyContract.id).subscribe({
                next: (blob: Blob) => {
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = `estado_cuenta_${this.historyContract.id}.pdf`;
                    a.click();
                    window.URL.revokeObjectURL(url);
                },
                error: err => console.error('Error downloading PDF', err)
            });
        }
    }
}
