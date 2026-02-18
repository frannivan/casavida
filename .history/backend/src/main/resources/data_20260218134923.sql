SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE user_roles;
TRUNCATE TABLE users;
TRUNCATE TABLE roles;
TRUNCATE TABLE lote_imagenes;
TRUNCATE TABLE pagos;
TRUNCATE TABLE contratos;
TRUNCATE TABLE clientes;
TRUNCATE TABLE lotes;
TRUNCATE TABLE fraccionamientos;
TRUNCATE TABLE role_permissions;
TRUNCATE TABLE permissions;
TRUNCATE TABLE modules;
TRUNCATE TABLE actions;

-- Ensure pagos table has newest columns (fallback for hibernate update)
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS validado BOOLEAN DEFAULT FALSE;
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS fecha_validacion TIMESTAMP;
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS validado_por VARCHAR(255);
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS metodo_pago VARCHAR(255);
ALTER TABLE pagos ADD COLUMN IF NOT EXISTS comprobante_content_type VARCHAR(255);

SET REFERENTIAL_INTEGRITY TRUE;

-- 1. Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (3, 'ROLE_VENDEDOR');
INSERT INTO roles (id, name) VALUES (4, 'ROLE_RECEPCION');
INSERT INTO roles (id, name) VALUES (5, 'ROLE_CONTABILIDAD');
INSERT INTO roles (id, name) VALUES (6, 'ROLE_DIRECTIVO');

-- 2. Users (Password: password123)
-- Hash: $2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou
INSERT INTO users (id, username, email, password) VALUES (1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (4, 'Admin_Test', 'admin_test@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (5, 'contabilidad', 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (6, 'directivo', 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');
INSERT INTO users (id, username, email, password) VALUES (7, 'cliente', 'cliente@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou');

-- 3. Assign Roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 4);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 5);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 6);
INSERT INTO user_roles (user_id, role_id) VALUES (7, 1);

-- 4. Fraccionamientos
INSERT INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador) 
VALUES (1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]');

INSERT INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador)
VALUES (2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

-- 5. Lotes
INSERT INTO lotes (id, numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates)
VALUES (1, 'A001', 'MZ A', 200.0, 150000.0, '21.1622, -86.8494', 'DISPONIBLE', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]');

INSERT INTO lotes (id, numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates)
VALUES (2, 'M001', 'Calle Mar', 500.0, 100000.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

-- 6. Clientes
INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro)
VALUES (1, 'Francisco', 'Iván', 'franivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP);

INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro)
VALUES (2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP);

-- 7. Contratos
INSERT INTO contratos (id, monto_total, enganche, plazo_meses, tasa_interes_anual, fecha_contrato, cliente_id, lote_id, vendedor_id, estatus)
VALUES (1, 150000.0, 30000.0, 12, 0.0, CURRENT_TIMESTAMP, 1, 1, 2, 'ACTIVO');

-- 8. Pagos (Enganche)
INSERT INTO pagos (id, monto, fecha_pago, concepto, referencia, metodo_pago, estatus, validado, contrato_id)
VALUES (1, 30000.0, CURRENT_TIMESTAMP, 'Enganche', 'REF-ENG-001', 'Transferencia', 'VALIDADO', true, 1);

-- Update Lote status to VENDIDO for Lote 1
UPDATE lotes SET estatus = 'VENDIDO' WHERE id = 1;

-- 9. Admin Client Profile (Fix for Dashboard)
INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro)
VALUES (7, 'Admin', 'Privileged User', 'admin@test.com', '5555555555', 'Oficina Central', 'INE-ADMIN-001', CURRENT_TIMESTAMP);

-- 10. Sample Contract for Admin (Fix for Dashboard Empty State)
INSERT INTO contratos (id, monto_total, enganche, plazo_meses, tasa_interes_anual, fecha_contrato, cliente_id, lote_id, vendedor_id, estatus)
VALUES (2, 100000.0, 10000.0, 12, 0.0, CURRENT_TIMESTAMP, 7, 2, 2, 'ACTIVO');

-- Update Lote 2 status
UPDATE lotes SET estatus = 'CONTRATADO' WHERE id = 2;

-- 11. Sample Payment for Admin Contract
INSERT INTO pagos (id, monto, fecha_pago, concepto, referencia, metodo_pago, estatus, validado, contrato_id)
VALUES (2, 5000.0, CURRENT_TIMESTAMP, 'Mensualidad 1', 'REF-ADM-001', 'Transferencia', 'VALIDADO', true, 2);

-- ========================================
-- RBAC SEED DATA - TEMPORARILY DISABLED
-- TODO: Add created_at, updated_at timestamps
-- ========================================

-- ACTIONS (generic actions across all modules)
INSERT INTO actions (id, name, display_name, description, created_at) VALUES
(1, 'VIEW', 'Ver', 'Visualizar información', CURRENT_TIMESTAMP),
(2, 'CREATE', 'Crear', 'Crear nuevos registros', CURRENT_TIMESTAMP),
(3, 'EDIT', 'Editar', 'Modificar registros existentes', CURRENT_TIMESTAMP),
(4, 'DELETE', 'Eliminar', 'Eliminar registros', CURRENT_TIMESTAMP),
(5, 'APPROVE', 'Aprobar', 'Validar/Aprobar registros', CURRENT_TIMESTAMP),
(6, 'EXPORT', 'Exportar', 'Exportar datos a archivos', CURRENT_TIMESTAMP);

-- MODULES
INSERT INTO modules (id, name, display_name, description, icon, display_order, active, created_at, updated_at) VALUES
(1, 'PAGOS', 'Gestión de Pagos', 'Módulo para registrar, validar y consultar pagos', 'payments', 1, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'CONTRATOS', 'Gestión de Contratos', 'Módulo para administrar contratos de venta', 'contracts', 2, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'CLIENTES', 'Gestión de Clientes', 'Módulo para administrar información de clientes', 'people', 3, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'REPORTES', 'Gestión de Reportes', 'Módulo para generar reportes dinámicos', 'assessment', 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'USUARIOS', 'Gestión de Usuarios', 'Módulo para administrar cuentas y roles', 'manage_accounts', 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'CRM', 'Gestión de Prospectos', 'Módulo para administrar leads y oportunidades', 'leaderboard', 6, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'CONFIG', 'Configuración del Sistema', 'Módulo para configuraciones globales y carga masiva', 'settings', 7, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR PAGOS
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(1, 'PAGOS.VIEW', 1, 1, 'Ver lista de pagos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'PAGOS.CREATE', 1, 2, 'Registrar nuevos pagos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'PAGOS.APPROVE', 1, 5, 'Aprobar/Rechazar pagos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR CONTRATOS
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(15, 'CONTRATOS.VIEW', 2, 1, 'Ver lista de contratos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'CONTRATOS.CREATE', 2, 2, 'Generar nuevos contratos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'CONTRATOS.EDIT', 2, 3, 'Editar información de contratos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR REPORTES
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(18, 'REPORTES.VIEW', 4, 1, 'Ver dashboard de reportes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'REPORTES.EXPORT', 4, 6, 'Exportar reportes a Excel/PDF', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR USUARIOS
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(20, 'USUARIOS.VIEW', 5, 1, 'Ver lista de usuarios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 'USUARIOS.CREATE', 5, 2, 'Crear nuevos usuarios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, 'USUARIOS.EDIT', 5, 3, 'Editar información de usuarios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 'USUARIOS.DELETE', 5, 4, 'Eliminar usuarios', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR CRM
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(24, 'CRM.VIEW', 6, 1, 'Ver leads y oportunidades', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'CRM.EDIT', 6, 3, 'Editar y convertir prospectos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR CONFIG
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(26, 'CONFIG.VIEW', 7, 1, 'Acceso a configuración y carga masiva', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);



-- PERMISSIONS FOR CLIENTES
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(4, 'CLIENTES.VIEW', 3, 1, 'Ver lista de clientes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'CLIENTES.CREATE', 3, 2, 'Registrar nuevos clientes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'CLIENTES.EDIT', 3, 3, 'Editar información de clientes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR INVENTARIO
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(7, 'INVENTARIO.VIEW', 3, 1, 'Ver inventario de lotes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'INVENTARIO.CREATE', 3, 2, 'Crear nuevos lotes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'INVENTARIO.EDIT', 3, 3, 'Editar información de lotes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'INVENTARIO.DELETE', 3, 4, 'Eliminar lotes', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- PERMISSIONS FOR FRACCIONAMIENTOS
INSERT INTO permissions (id, permission_key, module_id, action_id, description, active, created_at, updated_at) VALUES
(11, 'FRACCIONAMIENTOS.VIEW', 3, 1, 'Ver lista de fraccionamientos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'FRACCIONAMIENTOS.CREATE', 3, 2, 'Crear nuevos fraccionamientos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'FRACCIONAMIENTOS.EDIT', 3, 3, 'Editar información de fraccionamientos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'FRACCIONAMIENTOS.DELETE', 3, 4, 'Eliminar fraccionamientos', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);




-- ROLE PERMISSIONS (Mapping based on current data.sql roles: 2=ADMIN, 3=VENDEDOR, 4=RECEPCION, 5=CONTABILIDAD)
INSERT INTO role_permissions (role_id, permission_id, granted_at, granted_by) VALUES
(2, 1, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 2, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 3, CURRENT_TIMESTAMP, 'SYSTEM'),  -- ADMIN: PAGO permissions
(2, 4, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 5, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 6, CURRENT_TIMESTAMP, 'SYSTEM'),  -- ADMIN: CLIENTE permissions
(2, 7, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 8, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 9, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 10, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: INVENTARIO permissions
(2, 11, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 12, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 13, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 14, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: FRACCIONAMIENTOS permissions
(2, 15, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 16, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 17, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: CONTRATO permissions
(2, 18, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 19, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: REPORTE permissions
(2, 20, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 21, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 22, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 23, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: USUARIO permissions
(2, 24, CURRENT_TIMESTAMP, 'SYSTEM'), (2, 25, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: CRM permissions
(2, 26, CURRENT_TIMESTAMP, 'SYSTEM'), -- ADMIN: CONFIG permissions (Carga masiva)

(3, 1, CURRENT_TIMESTAMP, 'SYSTEM'), (3, 2, CURRENT_TIMESTAMP, 'SYSTEM'),                    -- VENDEDOR: PAGO view + create
(3, 4, CURRENT_TIMESTAMP, 'SYSTEM'), (3, 5, CURRENT_TIMESTAMP, 'SYSTEM'),                    -- VENDEDOR: CLIENTE view + create
(3, 7, CURRENT_TIMESTAMP, 'SYSTEM'),                                                         -- VENDEDOR: INVENTARIO view
(3, 11, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- VENDEDOR: FRACCIONAMIENTOS view
(3, 15, CURRENT_TIMESTAMP, 'SYSTEM'), (3, 16, CURRENT_TIMESTAMP, 'SYSTEM'),                  -- VENDEDOR: CONTRATO view + create
(3, 18, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- VENDEDOR: REPORTE view (dashboard)
(3, 24, CURRENT_TIMESTAMP, 'SYSTEM'), (3, 25, CURRENT_TIMESTAMP, 'SYSTEM'),                  -- VENDEDOR: CRM permissions
(4, 1, CURRENT_TIMESTAMP, 'SYSTEM'), (4, 2, CURRENT_TIMESTAMP, 'SYSTEM'),                    -- RECEPCION: PAGO view + create  
(4, 4, CURRENT_TIMESTAMP, 'SYSTEM'), (4, 5, CURRENT_TIMESTAMP, 'SYSTEM'),                    -- RECEPCION: CLIENTE view + create
(4, 7, CURRENT_TIMESTAMP, 'SYSTEM'),                                                         -- RECEPCION: INVENTARIO view
(4, 11, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- RECEPCION: FRACCIONAMIENTOS view
(4, 15, CURRENT_TIMESTAMP, 'SYSTEM'), (4, 16, CURRENT_TIMESTAMP, 'SYSTEM'),                  -- RECEPCION: CONTRATO view + create
(4, 24, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- RECEPCION: CRM view
(5, 1, CURRENT_TIMESTAMP, 'SYSTEM'), (5, 2, CURRENT_TIMESTAMP, 'SYSTEM'), (5, 3, CURRENT_TIMESTAMP, 'SYSTEM'),  -- CONTABILIDAD: PAGO permissions
(5, 4, CURRENT_TIMESTAMP, 'SYSTEM'),                                                         -- CONTABILIDAD: CLIENTE view only
(5, 7, CURRENT_TIMESTAMP, 'SYSTEM'),                                                         -- CONTABILIDAD: INVENTARIO view
(5, 11, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- CONTABILIDAD: FRACCIONAMIENTOS view
(5, 15, CURRENT_TIMESTAMP, 'SYSTEM'),                                                        -- CONTABILIDAD: CONTRATO view
(5, 18, CURRENT_TIMESTAMP, 'SYSTEM'), (5, 19, CURRENT_TIMESTAMP, 'SYSTEM');                  -- CONTABILIDAD: REPORTE view + export






