CREATE TABLE IF NOT EXISTS person (
    id serial PRIMARY KEY,
    firstname VARCHAR ( 256 ) NOT NULL,
    lastname VARCHAR ( 256 ) NOT NULL,
    email VARCHAR ( 256 ) NOT NULL,
    password VARCHAR ( 256 ) NOT NULL,
    role SMALLINT,
    stored boolean
);
INSERT INTO person (id, firstname, lastname, email, password, role, stored) VALUES (1, 'admin', 'localhost', 'admin@localhost.com', 'password', 0, true);
INSERT INTO person (id, firstname, lastname, email, password, role, stored) VALUES (2, 'user', 'localhost', 'user@localhost.com', 'password', 1, true);
ALTER SEQUENCE hibernate_sequence RESTART WITH 3;