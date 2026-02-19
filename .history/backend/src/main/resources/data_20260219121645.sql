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
