# 🏠 Visión de Experiencia: Venta de Lotes
> **Versión**: 1.0.4 | **Módulo**: Ciclo Comercial | **Tipo**: EIU (Experiencia de Usuario)

---

## 1. El Viaje del Cliente (Venta Visual)

El proceso de venta está diseñado para ser visualmente impactante, permitiendo al cliente elegir su patrimonio directamente sobre el mapa del fraccionamiento.

```mermaid
graph LR
    A[Búsqueda Lead] --> B[Mapa Interactivo]
    B --> C[Selección Lote]
    C --> D[Simulación Financiera]
    D --> E[Apartado / Pago]
```

---

## 2. Estados Visuales en el Mapa

La interfaz utiliza un código de colores universal (Semáforo) para indicar la disponibilidad:

| Color | Estatus | Significado Psicológico |
| :--- | :--- | :--- |
| 🟢 | **Disponible** | Oportunidad abierta, sensación de libertad. |
| 🟡 | **Apartado** | Urgencia, último momento para decidir. |
| 🔴 | **Vendido** | Seguridad, éxito de otros clientes. |

---

## 3. Interacción del Vendedor

El vendedor actúa como facilitador, operando el **[Simulador]** frente al cliente para ajustar el enganche y las mensualidades hasta llegar al punto de equilibrio financiero.

> [!TIP]
> Un buen vendedor siempre muestra el lote en el **[Mapa]** para generar un sentido de pertenencia y ubicación antes de pasar a los números.

---

## 4. Automatización del Cierre

Al completar el apartado, el sistema bloquea la unidad y dispara automáticamente:
1.  **Recibo de Apartado** al correo del cliente.
2.  **Notificación de Venta** al Administrador y Directivo.
3.  **Expediente Digital** listo para carga de documentos KYC.
