INSERT INTO persons (id, firstname, lastname, email, password, role, stored) VALUES (1, 'admin', 'localhost', 'admin@localhost.com', 'password', 0, true);
INSERT INTO persons (id, firstname, lastname, email, password, role, stored) VALUES (2, 'user', 'localhost', 'user@localhost.com', 'password', 1, true);
ALTER SEQUENCE hibernate_sequence RESTART WITH 3;