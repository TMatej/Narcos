CREATE TABLE IF NOT EXISTS medicine(
    id serial PRIMARY KEY,
    name VARCHAR ( 256 ) NOT NULL,
    manufacturer VARCHAR ( 256 ) NOT NULL,
    form SMALLINT,
    quantity SMALLINT NOT NULL,
    expirationDate DATE NOT NULL
);
INSERT INTO medicine (id, name, manufacturer, form, quantity, expirationdate) VALUES (1, 'Egilok', 'Egis Pharmaceuticals PLC', 1, 100, '2025-06-01');
INSERT INTO medicine (id, name, manufacturer, form, quantity, expirationdate) VALUES (2, 'Cardilan', 'Zentiva', 1, 100, '2019-12-01');
ALTER SEQUENCE hibernate_sequence RESTART WITH 3;