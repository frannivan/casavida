-- 1. Roles
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

-- 2. Admin User
-- Password: 'password' ($2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLq)
INSERT INTO users (username, email, password)
SELECT 'admin', 'admin@casavida.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLq'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- FORCE RESET PASSWORD (in case user already exists with old hash)
UPDATE users SET password = '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLq' WHERE username = 'admin';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

-- 3. Fraccionamientos
-- Residencial Las Palmas
INSERT INTO fraccionamientos (nombre, ubicacion, descripcion, logo_url, coordenadas_geo)
SELECT 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento con seguridad 24/7 y acceso a playa.', '/images/logos/palmas.png', '21.1619, -86.8515'
WHERE NOT EXISTS (SELECT 1 FROM fraccionamientos WHERE nombre = 'Residencial Las Palmas');

-- 7 Mares Residencial
INSERT INTO fraccionamientos (nombre, ubicacion, descripcion, logo_url, coordenadas_geo)
SELECT '7 Mares Residencial', 'Mazunte, Oaxaca', 'Desarrollo eco-turístico con vista al mar.', '/images/logos/7mares.png', '15.6665, -96.5556'
WHERE NOT EXISTS (SELECT 1 FROM fraccionamientos WHERE nombre = '7 Mares Residencial');

-- 4. Lotes
-- Helper variables not available in standard SQL script, using subqueries.

-- Lote A001
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'A001', 'Manzana A', 200.00, 150000.00, '21.1619, -86.8515', 'DISPONIBLE', '/images/lotes/lote-a001.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.', f.id
FROM fraccionamientos f WHERE f.nombre = 'Residencial Las Palmas'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'A001');

-- Lote A002
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'A002', 'Manzana A', 250.00, 180000.00, '21.1630, -86.8520', 'DISPONIBLE', '/images/lotes/lote-a002.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.', f.id
FROM fraccionamientos f WHERE f.nombre = 'Residencial Las Palmas'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'A002');

-- Lote B001
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'B001', 'Manzana B', 300.00, 220000.00, '21.1640, -86.8530', 'DISPONIBLE', '/images/lotes/lote-b001.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.', f.id
FROM fraccionamientos f WHERE f.nombre = 'Residencial Las Palmas'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'B001');

-- Lote B002
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'B002', 'Manzana B', 300.00, 220000.00, '21.1650, -86.8540', 'DISPONIBLE', '/images/lotes/lote-b002.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.', f.id
FROM fraccionamientos f WHERE f.nombre = 'Residencial Las Palmas'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'B002');

-- Lote C001 (VENDIDO)
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'C001', 'Manzana C', 500.00, 400000.00, '21.1660, -86.8550', 'VENDIDO', '/images/lotes/lote-c001.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento.', f.id
FROM fraccionamientos f WHERE f.nombre = 'Residencial Las Palmas'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'C001');

-- Lote M001 (7 Mares)
INSERT INTO lotes (numero_lote, manzana, area_metros_cuadrados, precio_total, coordenadas_geo, estatus, imagen_url, descripcion, fraccionamiento_id)
SELECT 'M001', 'Manzana Mar', 1000.00, 550000.00, '15.6665, -96.5556', 'DISPONIBLE', '/images/lotes/lote-a001.svg', 'Este lote cuenta con una excelente ubicación dentro del fraccionamiento, ideal para construir la casa de tus sueños. Cuenta con todos los servicios a pie de lote y acceso a las áreas comunes.', f.id
FROM fraccionamientos f WHERE f.nombre = '7 Mares Residencial'
AND NOT EXISTS (SELECT 1 FROM lotes WHERE numero_lote = 'M001');

-- Gallery Images (Assuming idempotency by checking existence isn't strictly necessary for ElementCollection if we trust logic, but safer to check)
-- Since ElementCollection tables usually don't have unique constraints on (lote_id, imagen_url) unless specified, we might duplicate.
-- But standard data.sql runs once if ddl-auto=create. If update, it runs every start?
-- spring.sql.init.mode=embedded (default) runs data.sql always.
-- We must be careful.
-- Strategy: Delete and re-insert for galleries if we want to be sure? No, that's destructive.
-- Verification query pattern:
INSERT INTO lote_imagenes (lote_id, imagen_url)
SELECT l.id, l.imagen_url FROM lotes l WHERE l.numero_lote = 'A001'
AND NOT EXISTS (SELECT 1 FROM lote_imagenes li WHERE li.lote_id = l.id AND li.imagen_url = l.imagen_url);

INSERT INTO lote_imagenes (lote_id, imagen_url)
SELECT l.id, 'https://placehold.co/600x400/2ecc71/FFF?text=Area+Verde' FROM lotes l WHERE l.numero_lote = 'A001'
AND NOT EXISTS (SELECT 1 FROM lote_imagenes li WHERE li.lote_id = l.id AND li.imagen_url = 'https://placehold.co/600x400/2ecc71/FFF?text=Area+Verde');

INSERT INTO lote_imagenes (lote_id, imagen_url)
SELECT l.id, 'https://placehold.co/600x400/e74c3c/FFF?text=Acceso+Principal' FROM lotes l WHERE l.numero_lote = 'A001'
AND NOT EXISTS (SELECT 1 FROM lote_imagenes li WHERE li.lote_id = l.id AND li.imagen_url = 'https://placehold.co/600x400/e74c3c/FFF?text=Acceso+Principal');

-- Repeated for other lotes (simplified for brevity: applying loop logic in SQL is hard, doing just A001 and C001 as examples from DataInitializer)
-- C001 Gallery
INSERT INTO lote_imagenes (lote_id, imagen_url)
SELECT l.id, l.imagen_url FROM lotes l WHERE l.numero_lote = 'C001'
AND NOT EXISTS (SELECT 1 FROM lote_imagenes li WHERE li.lote_id = l.id AND li.imagen_url = l.imagen_url);

-- 5. Client User & Profile
INSERT INTO users (username, email, password)
SELECT 'client', 'client@casavida.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlNBxBFve4ZlLq'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'client');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'client' AND r.name = 'ROLE_USER'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = u.id AND ur.role_id = r.id);

INSERT INTO clientes (nombre, apellidos, email, telefono, fecha_registro)
SELECT 'Juan', 'Perez Cliente', 'client@casavida.com', '555-000-1111', '2025-01-01T12:00:00'
WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE email = 'client@casavida.com');

-- 6. Contrato (Linked to C001 - VENDIDO)
-- Using fixed date '2025-10-15' as contract date (~3 months ago relative to jan 2026)
INSERT INTO contratos (cliente_id, lote_id, fecha_contrato, monto_total, estatus, mensualidad, enganche, plazo_meses, tasa_interes_anual)
SELECT c.id, l.id, '2025-10-15', l.precio_total, 'ACTIVO', 33333.33, 0.00, 12, 0.0
FROM clientes c, lotes l
WHERE c.email = 'client@casavida.com' AND l.numero_lote = 'C001'
AND NOT EXISTS (SELECT 1 FROM contratos WHERE lote_id = l.id);

-- 7. Pagos
-- Pago 1 (2 months ago) '2025-11-15'
INSERT INTO pagos (contrato_id, fecha_pago, monto, referencia, concepto)
SELECT co.id, '2025-11-15', 33333.33, 'SPEI-001', 'Mensualidad 1'
FROM contratos co JOIN lotes l ON co.lote_id = l.id
WHERE l.numero_lote = 'C001'
AND NOT EXISTS (SELECT 1 FROM pagos WHERE referencia = 'SPEI-001');

-- Pago 2 (1 month ago) '2025-12-15'
INSERT INTO pagos (contrato_id, fecha_pago, monto, referencia, concepto)
SELECT co.id, '2025-12-15', 33333.33, 'EFECTIVO-002', 'Mensualidad 2'
FROM contratos co JOIN lotes l ON co.lote_id = l.id
WHERE l.numero_lote = 'C001'
AND NOT EXISTS (SELECT 1 FROM pagos WHERE referencia = 'EFECTIVO-002');
