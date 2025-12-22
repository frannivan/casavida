# Manual Técnico - CasaVida

## 1. Arquitectura del Sistema
El sistema utiliza una arquitectura de microservicios monolíticos desacoplados (Frontend y Backend separados).

- **Backend**: Spring Boot 2.7.18 (Java 11).
    - **Base de Datos**: H2 (Archivo local `./data/casavida_db`).
    - **Seguridad**: Spring Security + JWT.
    - **Persistencia**: Spring Data JPA.
- **Frontend**: Angular 18.
    - **Estilos**: CSS personalizado con utilidades tipo Bootstrap.
    - **Comunicación**: HTTP Client con Proxy a Backend.

## 2. Requisitos de Instalación
- **Java JDK 11** o superior.
- **Node.js 18** o superior (Recomendado v20+).
- **Maven** (opcional, si no usa el wrapper).

## 3. Configuración y Ejecución

### Backend
1.  Navegar a la carpeta `backend`.
2.  Ejecutar: `mvn spring-boot:run`
    - El servidor iniciará en `http://localhost:8080`.
    - Consola H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/casavida_db`, User: `sa`, Pass: `password`).

### Frontend
1.  Navegar a la carpeta `frontend`.
2.  Instalar dependencias: `npm install`
3.  Ejecutar: `npm start`
    - La aplicación iniciará en `http://localhost:4200`.

## 4. Usuarios Pre-cargados (Seed Data)
El sistema crea automáticamente los Roles (`ROLE_USER`, `ROLE_ADMIN`).
Para crear un administrador inicial, puede registrarse mediante la API `/api/auth/signup` enviando el rol `admin` o insertar manualmente en la DB H2:
```sql
INSERT INTO users (username, email, password) VALUES ('admin', 'admin@casavida.com', '$2a$10$encodedPassword');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2); -- Asumiendo ID 2 es Admin
```

## 5. Solución de Problemas (Bugs y Fixes)
- **Error CORS**: Se configuró un Proxy en Angular (`proxy.conf.json`) para evitar problemas de CORS durante el desarrollo.
- **Spring Boot Version**: Se utilizó la versión 2.7.x por compatibilidad con Java 11 del entorno.
- **Angular Analytics**: Se deshabilitó la recolección de métricas para evitar bloqueos en la instalación desatendida.

## 6. Módulos Adicionales
### Reportes PDF
- **Librería**: `com.github.librepdf:openpdf` (v1.3.30).
- **Backend**:
    - `PdfService`: Genera el Blob del PDF.
    - `ReporteController`: 
        - `GET /api/reportes/dashboard`: Estadísticas generales.
        - `GET /api/reportes/estado-cuenta/{id}`: Descarga de archivo.
- **Frontend**:
    - `VentaService`: Manejo de `responseType: 'blob'` para la descarga.
    - Acceso: Modal de Historial en la tabla de Inventario.
