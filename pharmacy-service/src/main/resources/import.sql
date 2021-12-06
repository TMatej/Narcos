CREATE TABLE IF NOT EXISTS pharmacy (
    id serial PRIMARY KEY,
    name VARCHAR ( 256 ) NOT NULL,
    street VARCHAR ( 256 ) NOT NULL,
    streetNumber SMALLINT NOT NULL,
    city VARCHAR ( 256 ) NOT NULL
);
INSERT INTO pharmacy (id, name, street, streetNumber, city) VALUES (0, 'Doctor MAX', 'Main', 1, 'Brno');
INSERT INTO pharmacy (id, name, street, streetNumber, city) VALUES (1, 'Na rožku', 'Botanic', 26, 'Kosice');
ALTER SEQUENCE hibernate_sequence RESTART WITH 3;