INSERT INTO persons (id, firstname, lastname, email, password, role, stored) VALUES (1, 'example', 'domain', 'example@domain.com', 'password', 0, true);
INSERT INTO persons (id, firstname, lastname, email, password, role, stored) VALUES (2, 'Hannah', 'Backer', 'hannah.backer@domain.com', 'password123', 1, true);
ALTER SEQUENCE hibernate_sequence RESTART WITH 3;