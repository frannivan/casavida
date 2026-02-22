# 📊 Manual de Usuario: Contador y Analista BI
> **Versión**: 1.0.4 | **Módulo**: Business Intelligence | **Rol**: Contador / Auditor

---

## 1. Vigilancia de la Salud Financiera (Dashboard BI)

Como responsable contable, tu tablero principal ofrece una visión consolidada de la solvencia del proyecto.

1.  Navega a **[Reportes]** > **[Business Intelligence]**.
2.  **Métricas de Control**:
    *   📘 **Ingreso Mensual (Revenue)**: Total recaudado contra el objetivo de cobranza.
    *   📙 **Egresos & Comisiones**: Gastos operativos y comisiones devengadas por la fuerza de ventas.
3.  Si requieres un detalle granular, pulsa el botón **[Exportar Libro Mayor]** para descargar el desglose en Excel.

> [!NOTE]
> Los datos mostrados en este panel son netos, una vez que Recepción ha validado los comprobantes de pago.

![Panel de Analítica](images/manuals/reports_analytics_real.png)

---

## 2. Gestión de Cartera Vencida

El sistema identifica automáticamente a los clientes con retrasos en sus abonos mensuales.

1.  En la barra de herramientas, haz clic en el icono de **[Notificaciones]** (campana roja).
2.  Se desplegará la lista de clientes con saldos vencidos de más de 15 días.
3.  **Acción Directiva**: Pulsa sobre el nombre del cliente para abrir su expediente. Desde allí puedes presionar **[Enviar Recordatorio]** para disparar un mensaje de cobranza por WhatsApp o Email.

---

## 3. Cierre Mensual y Conciliación

1.  Verifica que no existan pagos en estatus **[Pendiente de Validación]** al final del día.
2.  Utiliza el botón **[Cerrar Periodo]** para congelar las comisiones de ese mes y prepararlas para pago.

> [!TIP]
> El sistema CasaVida calcula el interés moratorio configurado en el contrato de forma automática. El saldo mostrado al cliente en su portal ya incluye estos cargos.
