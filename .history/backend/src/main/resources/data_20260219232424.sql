-- DATA INITIALIZATION (ULTRA-ROBUST)

-- 1. Ensure Roles exist and get their IDs properly
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_USER');
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_ADMIN');
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_VENDEDOR');
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_RECEPCION');
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_CONTABILIDAD');
MERGE INTO roles (name) KEY (name) VALUES ('ROLE_DIRECTIVO');

-- 2. Cleanup and Recreate Main Users
DELETE FROM user_roles;
DELETE FROM "users";

-- Passwords: password123 ($2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou)
INSERT INTO "users" (id, username, email, password, created_at) VALUES 
(1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(4, 'Admin_Test', 'admin_test@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(101, 'franivan@test.com', 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(102, 'maria@test.com', 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);

-- 3. Link Roles dynamically by name to avoid ID mismatches
INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'admin' AND r.name IN ('ROLE_ADMIN', 'ROLE_USER');

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'vendedor' AND r.name = 'ROLE_VENDEDOR';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'recepcion' AND r.name = 'ROLE_RECEPCION';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'Admin_Test' AND r.name = 'ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'franivan@test.com' AND r.name = 'ROLE_USER';

INSERT INTO user_roles (user_id, role_id) 
SELECT u.id, r.id FROM "users" u, roles r WHERE u.username = 'maria@test.com' AND r.name = 'ROLE_USER';

-- 4. Fraccionamientos
DELETE FROM fraccionamientos;
INSERT INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador) VALUES 
(1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]'),
(2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

-- 5. Lotes
DELETE FROM lotes;
INSERT INTO lotes (id, numero_lote, manzana, precio_total, area_metros_cuadrados, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates) VALUES 
(1, 'A001', 'MZ A', 150000.0, 200.0, '21.1622, -86.8494', 'VENDIDO', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]'),
(2, 'M001', 'Calle Mar', 100000.0, 500.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

-- 6. Clientes
DELETE FROM clientes;
INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro, user_id) VALUES 
(1, 'Francisco', 'Iván', 'franivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP, 101),
(2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP, 102);

-- 7. Contratos
DELETE FROM contratos;
INSERT INTO contratos (id, monto_total, enganche, plazo_meses, tasa_interes_anual, fecha_contrato, cliente_id, lote_id, vendedor_id, estatus, mensualidad) VALUES 
(1, 150000.0, 30000.0, 12, 0.0, CURRENT_DATE, 1, 1, 2, 'ACTIVO', 10000.0);

-- 8. Pagos
DELETE FROM pagos;
INSERT INTO pagos (id, monto, fecha_pago, concepto, referencia, metodo_pago, estatus, validado, contrato_id) VALUES 
(1, 30000.0, CURRENT_DATE, 'Enganche', 'REF-ENG-001', 'Transferencia', 'VALIDADO', TRUE, 1);

-- RESET SEQUENCES
ALTER TABLE "users" ALTER COLUMN id RESTART WITH 200;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 10;
ALTER TABLE contratos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE pagos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE fraccionamientos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE lotes ALTER COLUMN id RESTART WITH 10;
