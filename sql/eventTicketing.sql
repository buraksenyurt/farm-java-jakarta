/*
    Jakart Event Ticketing örneğinde kullanılan veritabanı (PostgreSQL)
    tablo ve örnek verilerini içeren SQL dosyası.
*/
CREATE TABLE IF NOT EXISTS events (
    id  BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    total_seats INTEGER NOT NULL,
    seats_sold INTEGER NOT NULL DEFAULT 0,
    ticket_price NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    wallet_balance NUMERIC(10, 2) NOT NULL DEFAULT 0.00
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    seat_count INTEGER NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    booking_time TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS booking_attempts (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id),
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    seat_count INTEGER NOT NULL,
    status VARCHAR(25) NOT NULL,
    failure_reason VARCHAR(500),
    attempt_time TIMESTAMP NOT NULL DEFAULT now()
);

-- Örnek veriler
TRUNCATE TABLE booking_attempts, bookings, events, customers RESTART IDENTITY CASCADE;

INSERT INTO events (name, total_seats, seats_sold, ticket_price) VALUES
('Java Conference 2024', 100, 0, 150.00),
('Spring Boot Workshop', 50, 0, 75.00),
('Microservices Summit', 200, 0, 200.00);

INSERT INTO customers (full_name, email, wallet_balance) VALUES
('Mayk Ceyms', 'mayk.ceyms@example.com', 100.00),
('Ana Kurinikova', 'ana.kurinikova@example.com', 150.00),
('Beti Deyvis', 'beti.deyvis@example.com', 200.00),
('Cuilermo El Torro', 'cuilermo.el.torro@example.com', 50.00);