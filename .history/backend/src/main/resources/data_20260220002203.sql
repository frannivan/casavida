-- FINAL ATTEMPT AT DATA INITIALIZATION (Strict Cleanup & Fresh Setup)

-- 1. Roles
MERGE INTO roles (id, name) KEY (id) VALUES (1, 'ROLE_USER');
MERGE INTO roles (id, name) KEY (id) VALUES (2, 'ROLE_ADMIN');
MERGE INTO roles (id, name) KEY (id) VALUES (3, 'ROLE_VENDEDOR');
MERGE INTO roles (id, name) KEY (id) VALUES (4, 'ROLE_RECEPCION');
MERGE INTO roles (id, name) KEY (id) VALUES (5, 'ROLE_CONTABILIDAD');
MERGE INTO roles (id, name) KEY (id) VALUES (6, 'ROLE_DIRECTIVO');

-- 2. Clean Up (Order matters due to constraints)
DELETE FROM pagos;
DELETE FROM contratos;
DELETE FROM opportunities;
DELETE FROM leads;
DELETE FROM user_roles;
DELETE FROM clientes;
DELETE FROM "users";
DELETE FROM lotes;
DELETE FROM fraccionamientos;

-- 3. Users
-- Passwords: password123 ($2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou)
INSERT INTO "users" (id, username, email, password, created_at) VALUES 
(1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(4, 'contabilidad', 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(5, 'directivo', 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(101, 'franivan@test.com', 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP),
(102, 'maria@test.com', 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);

-- 4. Link Roles (EXACTLY ONE PER USER)
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);   -- Admin
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);   -- Vendedor
INSERT INTO user_roles (user_id, role_id) VALUES (3, 4);   -- Recepcion
INSERT INTO user_roles (user_id, role_id) VALUES (4, 5);   -- Contabilidad
INSERT INTO user_roles (user_id, role_id) VALUES (5, 6);   -- Directivo
INSERT INTO user_roles (user_id, role_id) VALUES (101, 1); -- Cliente 1
INSERT INTO user_roles (user_id, role_id) VALUES (102, 1); -- Cliente 2

-- 5. Fraccionamientos
INSERT INTO fraccionamientos (id, nombre, ubicacion, descripcion, logo_url, coordenadas_geo, poligono_delimitador) VALUES 
(1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]'),
(2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]');

-- 6. Lotes
INSERT INTO lotes (id, numero_lote, manzana, precio_total, area_metros_cuadrados, coordenadas_geo, estatus, fraccionamiento_id, plano_coordinates) VALUES 
(1, 'A001', 'MZ A', 150000.0, 200.0, '21.1622, -86.8494', 'VENDIDO', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]'),
(2, 'M001', 'Calle Mar', 100000.0, 500.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]');

-- 7. Clientes
INSERT INTO clientes (id, nombre, apellidos, email, telefono, direccion, ine, fecha_registro, user_id) VALUES 
(1, 'Francisco', 'Iván', 'franivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP, 101),
(2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP, 102);

-- RESET SEQUENCES
ALTER TABLE "users" ALTER COLUMN id RESTART WITH 200;
ALTER TABLE clientes ALTER COLUMN id RESTART WITH 10;
ALTER TABLE fraccionamientos ALTER COLUMN id RESTART WITH 10;
ALTER TABLE lotes ALTER COLUMN id RESTART WITH 10;


