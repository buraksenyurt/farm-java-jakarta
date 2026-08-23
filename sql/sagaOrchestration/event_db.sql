-- ==========================================================================================
-- event_db — Event Service'in kendi veritabanı
-- Mevcut java-town-postgres container'ı içinde YENİ bir veritabanı olarak kurulur.
-- Çalıştırma: docker exec -i java-town-postgres psql -U johndoe -d postgres < event_db.sql
-- ==========================================================================================

CREATE DATABASE event_db;

\c event_db

CREATE TABLE events (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(255)   NOT NULL,
    total_seats  INTEGER        NOT NULL,
    seats_sold   INTEGER        NOT NULL DEFAULT 0,
    ticket_price NUMERIC(10,2)  NOT NULL
);

INSERT INTO events (name, total_seats, seats_sold, ticket_price) VALUES
    ('Jakarta EE Konferansı 2026', 100, 95, 150.00),
    ('Java Zirvesi', 50, 10, 80.00),
    ('Çok Yaşa Ryst', 200, 150, 120.00);