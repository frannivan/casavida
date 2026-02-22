-- FINAL ATTEMPT AT DATA INITIALIZATION (Nuclear Cleanup & Fresh Setup)

-- Disable integrity checks to allow clean slate
SET REFERENTIAL_INTEGRITY FALSE;

-- 1. Roles
MERGE INTO roles (id, name) KEY (id) VALUES (1, 'ROLE_USER');
MERGE INTO roles (id, name) KEY (id) VALUES (2, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY (id) VALUES (3, 'ROLE_VENDEDOR');
MERGE INTO roles (id, name) KEY (id) VALUES (4, 'ROLE_RECEPCION');
MERGE INTO roles (id, name) KEY (id) VALUES (5, 'ROLE_CONTABILIDAD');
MERGE INTO roles (id, name) KEY (id) VALUES (6, 'ROLE_DIRECTIVO');
MERGE INTO roles (id, name) KEY (id) VALUES (7, 'ROLE_SOPORTE');

-- 2. Massive Clean Up
DELETE FROM mensajes;
DELETE FROM pagos;
DELETE FROM contratos;
DELETE FROM opportunities;
DELETE FROM leads;
DELETE FROM user_roles;
DELETE FROM clientes;
DELETE FROM USERS;
DELETE FROM lotes;
DELETE FROM fraccionamientos;

-- Re-enable integrity checks
SET REFERENTIAL_INTEGRITY TRUE;

-- 3. Users (FRESH START)
-- Passwords: password123 ($2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou)
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (4, 'contabilidad', 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (5, 'directivo', 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (6, 'soporte', 'soporte@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (101, 'franivan@test.com', 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO USERS (id, username, email, password, created_at) KEY (id) VALUES (102, 'maria@test.com', 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);

-- 4. Link Roles
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (1, 2);   -- Admin
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (2, 3);   -- Vendedor
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (3, 4);   -- Recepcion
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (4, 5);   -- Contabilidad
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (5, 6);   -- Directivo
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (6, 7);   -- Soporte
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (101, 1); -- Cliente
MERGE INTO user_roles (user_id, role_id) KEY (user_id, role_id) VALUES (102, 1); -- Cliente

-- 5. Entities
INSERT INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador) VALUES 
(1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]'),
(2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

INSERT INTO lotes (id, numero_lote, manzana, precio_total, area_metros_cuadrados, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates) VALUES 
(1, 'A001', 'MZ A', 150000.0, 200.0, '21.1622, -86.8494', 'VENDIDO', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]'),
(2, 'M001', 'Calle Mar', 100000.0, 500.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro, user_id) VALUES 
(1, 'Francisco', 'Iván', 'franivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP, 101),
(2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP, 102);

-- RESET SEQUENCES
ALTER TABLE USERS ALTER COLUMN id RESTART WITH 200;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 10;
ALTER TABLE fraccionamientos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE lotes ALTER COLUMN id RESTART WITH 10;

-- 7. Sample Messages (Internal + CRM)
-- Internal messages between users
INSERT INTO mensajes (id, tipo, direccion, contenido, remitente, fecha, asunto, leido, remitente_user_id, destinatario_user_id) VALUES
(1, 'INTERNO', 'ENVIADO', 'Hola Admin, quería comentarte que el lote A001 ya fue visitado por el cliente.', 'vendedor', PARSEDATETIME('2026-02-20 09:30:00', 'yyyy-MM-dd HH:mm:ss'), 'Actualización Lote A001', false, 2, 1),
(2, 'INTERNO', 'ENVIADO', 'Perfecto, gracias por el aviso. ¿El cliente mostró interés en otros lotes?', 'admin', PARSEDATETIME('2026-02-20 10:15:00', 'yyyy-MM-dd HH:mm:ss'), 'Re: Actualización Lote A001', true, 1, 2),
(3, 'INTERNO', 'ENVIADO', 'El pago del mes de enero del cliente Francisco está pendiente de verificación.', 'recepcion', PARSEDATETIME('2026-02-19 14:00:00', 'yyyy-MM-dd HH:mm:ss'), 'Pago pendiente de verificar', false, 3, 1),
(4, 'INTERNO', 'ENVIADO', 'Por favor confirmen si el lote M001 sigue disponible, tengo un cliente interesado.', 'vendedor', PARSEDATETIME('2026-02-21 08:00:00', 'yyyy-MM-dd HH:mm:ss'), 'Disponibilidad Lote M001', false, 2, 3),
(5, 'INTERNO', 'ENVIADO', 'Adjunto el reporte de ventas del mes pasado para revisión.', 'contabilidad', PARSEDATETIME('2026-02-18 16:30:00', 'yyyy-MM-dd HH:mm:ss'), 'Reporte de Ventas - Enero 2026', false, 4, 5);

-- CRM messages (WhatsApp and Email with Leads)
INSERT INTO mensajes (id, target_id, tipo, direccion, contenido, remitente, fecha, leido) VALUES
(6, 1, 'WA', 'ENVIADO', 'Hola, le escribimos de CasaVida. ¿Sigue interesado en el lote A001?', 'Vendedor', PARSEDATETIME('2026-02-20 11:00:00', 'yyyy-MM-dd HH:mm:ss'), false),
(7, 1, 'WA', 'RECIBIDO', 'Sí, me gustaría agendar una visita esta semana.', 'Cliente', PARSEDATETIME('2026-02-20 11:05:00', 'yyyy-MM-dd HH:mm:ss'), false),
(8, 1, 'WA', 'ENVIADO', 'Perfecto, ¿le parece el jueves a las 10am?', 'Vendedor', PARSEDATETIME('2026-02-20 11:10:00', 'yyyy-MM-dd HH:mm:ss'), false),
(9, 1, 'EMAIL', 'ENVIADO', 'ASUNTO: Cotización Lote A001\n\nEstimado cliente,\n\nAdjunto encontrará la cotización del Lote A001 en Residencial Las Palmas.\n\nPrecio: $150,000 MXN\nÁrea: 200 m²\n\nQuedamos a sus órdenes.', 'Vendedor', PARSEDATETIME('2026-02-19 15:00:00', 'yyyy-MM-dd HH:mm:ss'), false);

ALTER TABLE mensajes ALTER COLUMN id RESTART WITH 100;
