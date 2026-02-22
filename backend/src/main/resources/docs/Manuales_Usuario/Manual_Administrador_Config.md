# ⚙️ Manual de Usuario: Administrador del Sistema
> **Versión**: 1.0.4 | **Módulo**: Configuración Global | **Rol**: Administrador / IT

---

## 1. Gestión de Seguridad y Roles (RBAC)

El Administrador tiene el control total sobre los privilegios de acceso al ERP.

1.  Navega a **[Configuración]** > **[Usuarios y Seguridad]**.
2.  Podrás visualizar la tabla de usuarios activos y sus roles asignados.
3.  **Interruptores de Permiso**: Usa los controles **[Toggle]** para habilitar o restringir funciones críticas (Ej: Capacidad de Borrar, Exportar a Excel o Ver Reportes de Ventas).

> [!IMPORTANT]
> Los cambios en los permisos de un rol afectan a todos los usuarios vinculados a ese rol de manera inmediata.

![Gestión de Usuarios](images/manuals/user_roles_real.png)

---

## 2. Implementación de Nuevos Fraccionamientos

Flujo de trabajo para expandir el inventario:

1.  **Carga de Mapa**: Sube el archivo base en formato **[SVG]** dentro del módulo de inventario.
2.  **Carga Masiva de Datos**: Utiliza la plantilla de Excel para importar los números de lote, manzanas, precios y dimensiones.
3.  **Sincronización**: El sistema ejecutará un motor de validación para asegurar que cada polígono del mapa tenga un registro correspondiente en la base de datos.

---

## 3. Auditoría y Mejora Continua

*   **Logs del Chatbot**: Revisa periódicamente las interacciones de la IA con los clientes para identificar preguntas frecuentes no resueltas.
*   **Base de Conocimientos**: Si el Chatbot responde de forma inexacta, actualiza el módulo de **[Conocimientos IA]** con la información correcta.

> [!TIP]
> Se recomienda realizar un respaldo de la base de datos (Exportar Todo) al menos una vez por semana desde el panel de **[Mantenimiento]**.
