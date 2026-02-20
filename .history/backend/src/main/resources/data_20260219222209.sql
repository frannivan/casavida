-- 1. Roles
MERGE INTO roles KEY (id) VALUES (1, 'ROLE_USER');
MERGE INTO roles KEY (id) VALUES (2, 'ROLE_ADMIN');
MERGE INTO roles KEY (id) VALUES (3, 'ROLE_VENDEDOR');
MERGE INTO roles KEY (id) VALUES (4, 'ROLE_RECEPCION');
MERGE INTO roles KEY (id) VALUES (5, 'ROLE_CONTABILIDAD');
MERGE INTO roles KEY (id) VALUES (6, 'ROLE_DIRECTIVO');

-- 2. Users (Password: password123)
MERGE INTO users KEY (id) VALUES (1, 'admin', 'admin@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (2, 'vendedor', 'vendedor@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (3, 'recepcion', 'recepcion@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (4, 'Admin_Test', 'admin_test@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (5, 'contabilidad', 'contabilidad@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (6, 'directivo', 'directivo@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (7, 'cliente_test', 'cliente@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (101, 'franivan@test.com', 'franivan@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);
MERGE INTO users KEY (id) VALUES (102, 'maria@test.com', 'maria@test.com', '$2a$10$qbVHd72aWuXZTIrAm75aIud8plhMfJKbdps00KbjmqDrniX4bcpou', CURRENT_TIMESTAMP);

-- 3. Assign Roles (Using compound key logic via subquery or direct merge if table allows)
-- For user_roles, manual clean or check is better as it lacks simple PK
DELETE FROM user_roles; 
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (2, 3);
INSERT INTO user_roles (user_id, role_id) VALUES (3, 4);
INSERT INTO user_roles (user_id, role_id) VALUES (4, 2);
INSERT INTO user_roles (user_id, role_id) VALUES (5, 5);
INSERT INTO user_roles (user_id, role_id) VALUES (6, 6);
INSERT INTO user_roles (user_id, role_id) VALUES (7, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (101, 1);
INSERT INTO user_roles (user_id, role_id) VALUES (102, 1);

-- 4. Fraccionamientos
MERGE INTO fraccionamientos KEY (id) VALUES (1, 'Residencial Las Palmas', 'Cancún, Quintana Roo', 'Exclusivo fraccionamiento.', '/casavida/api/images/palmas.png', '21.1619, -86.8515', '[[21.162795,-86.849289],[21.162170,-86.848870],[21.161319,-86.850695],[21.161560,-86.850947],[21.161840,-86.850936],[21.162055,-86.850850],[21.162795,-86.849321]]', NULL, NULL);
MERGE INTO fraccionamientos KEY (id) VALUES (2, '7 Mares Residencial', 'Mazunte, Oaxaca', 'Eco-turístico.', '/casavida/api/images/7mares.png', '15.6665, -96.5556', '[[15.667814,-96.555597],[15.667711,-96.555114],[15.665727,-96.555081],[15.665992,-96.555699]]', NULL, NULL);

-- 5. Lotes
MERGE INTO lotes KEY (id) VALUES (1, 'A001', 'MZ A', 200.0, 150000.0, '21.1622, -86.8494', 'VENDIDO', 1, '[[21.162237,-86.849375],[21.162157,-86.849568],[21.161922,-86.849423],[21.162032,-86.849224],[21.162222,-86.849364]]', NULL);
MERGE INTO lotes KEY (id) VALUES (2, 'M001', 'Calle Mar', 500.0, 100000.0, '15.6665, -96.5556', 'DISPONIBLE', 2, '[[15.666449,-96.555699],[15.666444,-96.555477],[15.665992,-96.555482],[15.665990,-96.555750],[15.666418,-96.555705]]', NULL);

-- 6. Clientes
MERGE INTO clientes KEY (id) VALUES (1, 'Francisco', 'Iván', 'franivan@test.com', '9981234567', 'Av. Kabah #123, Cancún', 'INE12345678', CURRENT_TIMESTAMP, 101);
MERGE INTO clientes KEY (id) VALUES (2, 'María', 'García', 'maria@test.com', '9987654321', 'Colonia Centro, Mazunte', 'INE87654321', CURRENT_TIMESTAMP, 102);

-- 7. Contratos
MERGE INTO contratos KEY (id) VALUES (1, 150000.0, 30000.0, 12, 0.0, CURRENT_TIMESTAMP, 1, 1, 2, 'ACTIVO');

-- 8. Pagos
MERGE INTO pagos KEY (id) VALUES (1, 30000.0, CURRENT_TIMESTAMP, 'Enganche', 'REF-ENG-001', 'Transferencia', 'VALIDADO', true, 1, NULL, NULL, NULL);
