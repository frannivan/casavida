# Manual de Usuario - CasaVida

Bienvenido al sistema **CasaVida**, la plataforma integral para la gestión de terrenos y ventas inmobiliarias.

## 1. Acceso al Sistema

### Portal Público
Al ingresar al sistema (por defecto en `http://localhost:4200`), verás la **Página de Inicio**.
- **Visualización de Lotes**: Podrás ver un catálogo de los lotes disponibles.
- **Filtros de Búsqueda**: Utiliza la barra superior para filtrar lotes por:
    - **Fraccionamiento**: Selecciona un desarrollo específico (ej. "Residencial Casa Vida", "7 Mares").
    - **Ubicación**: Selecciona la ciudad o zona de interés.
    - **Precio**: Ordena los resultados de menor a mayor o viceversa.
- **Detalles y Cotización**: Haz clic en "Ver Detalles" para ver fotos y más info. Si te interesa, usa el botón **"Cotizar Ahora"** para enviar tus datos y recibir información personalizada.

### Registro e Inicio de Sesión
Para acceder a funciones avanzadas o administrativas:
1.  Haz clic en **Registrarse** en la barra de navegación.
2.  Ingresa un Usuario, Email y Contraseña.
3.  Una vez registrado, haz clic en **Ingresar**.
4.  Introduce tus credenciales.

> **Nota**: El primer usuario registrado via API o base de datos puede tener rol de ADMIN si se configura, o usar los usuarios pre-cargados (ver Manual Técnico).

## 2. Portal Administrativo
Si tu usuario tiene rol de **ADMIN**, verás la opción "Panel Admin" en el menú.

### Gestión de Inventario
- **Dashboard Principal**: Al ingresar, verás tarjetas con indicadores clave:
    - **Lotes Disponibles**: Total de terrenos listos para venta.
    - **Lotes Vendidos**: Total de cierres exitosos.
    - **Total Clientes**: Usuarios registrados en la plataforma.
    - **Contratos Activos**: Negocios en curso.
    - **Ingresos Totales**: Suma acumulada de todos los pagos recibidos.
    - **Saldo Pendiente**: Monto total por cobrar de los contratos activos.
- **Ventas Recientes**: Tabla con los últimos 5 contratos firmados.
- **Ver Inventario**: Tabla detallada con todos los lotes debajo de las métricas.

- **Crear Lote**:
    1.  Haz clic en el botón "Nuevo Lote".
    2.  Llena el formulario con Número, Manzana, Precio, Área.
    3.  Haz clic en "Guardar". El lote aparecerá inmediatamente como "DISPONIBLE".

- **Historial de Lote y Reportes**:
    - Haz clic sobre cualquier fila del inventario (Lote) para abrir su **Historial**.
    - Verás los datos del Contrato, tabla de pagos y el botón **"Descargar PDF"** para obtener el Estado de Cuenta.

### Gestión de Clientes (Nuevo)
El sistema cuenta ahora con un módulo dedicado para la administración de clientes. Puedes acceder desde la opción **"Clientes"** en el menú superior o haciendo clic en la tarjeta **"Total Clientes"** del Dashboard.

- **Directorio de Clientes**: Visualiza una lista completa de todos los clientes registrados.
- **Nuevo Cliente**: Haz clic en "Nuevo Cliente" para registrar uno desde cero.
- **Editar Cliente**: Identifica al cliente en la lista y pulsa el botón **Editar** (amarillo) para modificar sus datos personales o de contacto.

### Credenciales de Acceso al Panel Administrativo
Para ingresar al panel administrativo con credenciales por defecto:
*   **Usuario**: `admin`
*   **Contraseña**: `password`
*   **Rol**: Administrador General

### Gestión de Ventas y Pagos
El Panel Administrativo permite gestionar las transacciones:

1.  **👤 Registrar Cliente**:
    - Te redirige al nuevo módulo de Clientes para dar de alta uno nuevo.

2.  **📝 Generar Contrato**:
    - Haz clic en el botón verde para formalizar una venta.
    - Selecciona al **Cliente** (registrado previamente) y un **Lote Disponible** del inventario.
    - Define las condiciones financieras (Enganche, Plazo, Mensualidad) y guarda.
    - **Resultado**: El lote cambiará automáticamente a estatus **VENDIDO**.

3.  **💳 Registrar Pago**:
    - Haz clic en el botón cian para capturar un ingreso.
    - Selecciona primero al **Cliente** para filtrar sus datos.
    - Selecciona el **Contrato Activo** correspondiente (verás el Lote y Desarrollo).
    - Ingresa el Monto, Referencia (Cheque, Transferencia) y Concepto (ej. Mensualidad 1).
    - **Resultado**: El pago se refleja inmediatamente en el Estado de Cuenta del cliente.

## 3. Portal de Cliente
Si eres un cliente con contrato activo y tu usuario tiene el rol **ROLE_USER**:
- Verás la opción **"Panel Cliente"** en la barra de navegación superior.
- Al ingresar, verás un dashboard con:
    - Tu nombre y datos de contacto.
    - **Mis Propiedades y Contratos**: Lista de lotes que has adquirido o apartado, mostrando el estatus actual (DISPONIBLE, APARTADO, VENDIDO).
    - **Detalles del Contrato**: Fecha de compra y desarrollo al que pertenece.
- **Estado de Cuenta**: Haz clic en el botón "Ver Estado de Cuenta" dentro de la tarjeta del contrato para abrir una ventana detallada con:
    - Resumen del desarrollo y número de lote.
    - Tabla completa de pagos realizados (Fecha, Concepto, Referencia, Monto).
    - Botón **"Descargar PDF"** para obtener el archivo físico del Estado de Cuenta.
    - **Total Pagado** actualizado.

