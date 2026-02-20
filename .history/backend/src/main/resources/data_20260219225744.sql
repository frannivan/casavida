-- DEFINITIVE DATA INITIALIZATION (UPSERT PATTERN)

-- 0. Cleanup Junction Table (always replace to ensure clean role mapping)
DELETE FROM user_roles;

-- 1. Roles
MERGE INTO roles (id, name) KEY (id) VALUES (1, 'ROLE_USER');
MERGE INTO roles (id, name) KEY (id) VALUES (2, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY (id) VALUES (3, 'ROLE_VENDEDOR');
MERGE INTO roles (id, name) KEY (id) VALUES (4, 'ROLE_RECEPCION');
MERGE INTO roles (id, name) KEY (id) VALUES (5, 'ROLE_CONTABILIDAD');
MERGE INTO roles (id, name) KEY (id) VALUES (6, 'ROLE_DIRECTIVO');

-- 2. "Users" (Using quoted name "users" to avoid reserved word issues)
-- We use email as a secondary key check or just stick to ID if we are sure of the structure
MERGE INTO "users" (id, username, email, password, created_at) KEY (id) VALUES 
(1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(4, 'Admin_Test', 'admin_test@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(5, 'contabilidad', 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(6, 'directivo', 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(7, 'cliente_test', 'cliente@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);

-- Target Users 101 and 102
MERGE INTO "users" (id, username, email, password, created_at) KEY (id) VALUES 
(101, 'franivan@test.com', 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(102, 'maria@test.com', 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);


-- 3. Link Users to Roles
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 2), (2, 3), (3, 4), (4, 2), (5, 5), (6, 6), (7, 1), (101, 1), (102, 1);

-- 4. Fraccionamientos
MERGE INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador) KEY (id) VALUES 
(1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]'),
(2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

-- 5. Lotes (Corrected column selection to match entity)
MERGE INTO lotes (id, numero_lote, manzana, precio_total, area_metros_cuadrados, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates) KEY (id) VALUES 
(1, 'A001', 'MZ A', 150000.0, 200.0, '21.1622, -86.8494', 'VENDIDO', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]'),
(2, 'M001', 'Calle Mar', 100000.0, 500.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

-- 6. Clientes
MERGE INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro, user_id) KEY (id) VALUES 
(1, 'Francisco', 'Iván', 'frivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP, 101),
(2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP, 102);

-- 7. Contratos
MERGE INTO contratos (id, monto_total, enganche, plazo_meses, tasa_interes_anual, fecha_contrato, cliente_id, lote_id, vendedor_id, estatus, mensualidad) KEY (id) VALUES 
(1, 150000.0, 30000.0, 12, 0.0, CURRENT_DATE, 1, 1, 2, 'ACTIVO', 10000.0);

-- 8. Pagos
MERGE INTO pagos (id, monto, fecha_pago, concepto, referencia, metodo_pago, estatus, validado, contrato_id) KEY (id) VALUES 
(1, 30000.0, CURRENT_DATE, 'Enganche', 'REF-ENG-001', 'Transferencia', 'VALIDADO', TRUE, 1);

-- RESET SEQUENCES (Critical for H2 to allow subsequent UI creations)
ALTER TABLE "users" ALTER COLUMN id RESTART WITH 200;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 10;
ALTER TABLE contratos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE pagos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE fraccionamientos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE lotes ALTER COLUMN id RESTART WITH 10;
