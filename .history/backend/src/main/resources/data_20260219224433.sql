-- Definitive data.sql with named columns for UPSERT logic (MERGE INTO)

-- 1. Roles
MERGE INTO roles (id, name) KEY (id) VALUES (1, 'ROLE_USER');
MERGE INTO roles (id, name) KEY (id) VALUES (2, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY (id) VALUES (3, 'ROLE_VENDEDOR');
MERGE INTO roles (id, name) KEY (id) VALUES (4, 'ROLE_RECEPCION');
MERGE INTO roles (id, name) KEY (id) VALUES (5, 'ROLE_CONTABILIDAD');
MERGE INTO roles (id, name) KEY (id) VALUES (6, 'ROLE_DIRECTIVO');

-- 2. Users (Password: password123)
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (1, CURRENT_TIMESTAMP, 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'admin');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (2, CURRENT_TIMESTAMP, 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'vendedor');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (3, CURRENT_TIMESTAMP, 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'recepcion');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (4, CURRENT_TIMESTAMP, 'admin_test@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'Admin_Test');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (5, CURRENT_TIMESTAMP, 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'contabilidad');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (6, CURRENT_TIMESTAMP, 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'directivo');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (7, CURRENT_TIMESTAMP, 'cliente@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'cliente_test');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (101, CURRENT_TIMESTAMP, 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'franivan@test.com');
MERGE INTO users (id, created_at, email, password, username) KEY (id) VALUES (102, CURRENT_TIMESTAMP, 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', 'maria@test.com');

-- 3. Assign Roles (Using DELETE/INSERT for junction table)
DELETE FROM user_roles;
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2), (2, 3), (3, 4), (4, 2), (5, 5), (6, 6), (7, 1), (101, 1), (102, 1);

-- 4. Fraccionamientos
MERGE INTO fraccionamientos (id, descripcion, logo_url, nombre, ubicacion, coordenadas_geo, poligono_delimitador) KEY (id) VALUES 
(1, 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', 'Residencial Las Palmas', 'Cancún, Quintana Roo', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]'),
(2, 'Eco-turístico.', '/casavida/api/images/7mares.png', '7 Mares Residencial', 'Mazunte, Oaxaca', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

-- 5. Lotes
MERGE INTO lotes (id, area_metros_cuadrados, coordenadas_geo, estatus, manzana, numero_lote, precio_total, fraccionamiento_id, plano_coordinates) KEY (id) VALUES 
(1, 200.0, '21.1622, -86.8494', 'VENDIDO', 'MZ A', 'A001', 150000.0, 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]'),
(2, 500.0, '15.6665, -96.5556', 'DISPONIBLE', 'Calle Mar', 'M001', 100000.0, 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

-- 6. Clientes
MERGE INTO clientes (id, apellidos, email, fecha_registro, nombre, telefono, user_id, direccion, ine) KEY (id) VALUES 
(1, 'Iván', 'franivan@test.com', CURRENT_TIMESTAMP, 'Francisco', '9981234567', 101, 'Av. Kabah #123, Cancún', 'INE12345678'),
(2, 'García', 'maria@test.com', CURRENT_TIMESTAMP, 'María', '9987654321', 102, 'Colonia Centro, Mazunte', 'INE87654321');

-- 7. Contratos
MERGE INTO contratos (id, enganche, estatus, fecha_contrato, monto_total, plazo_meses, tasa_interes_anual, cliente_id, lote_id, vendedor_id, mensualidad) KEY (id) VALUES 
(1, 30000.0, 'ACTIVO', CURRENT_DATE, 150000.0, 12, 0.0, 1, 1, 2, 10000.0);

-- 8. Pagos
MERGE INTO pagos (id, concepto, estatus, fecha_pago, monto, referencia, validado, contrato_id, metodo_pago) KEY (id) VALUES 
(1, 'Enganche', 'VALIDADO', CURRENT_DATE, 30000.0, 'REF-ENG-001', TRUE, 1, 'Transferencia');
