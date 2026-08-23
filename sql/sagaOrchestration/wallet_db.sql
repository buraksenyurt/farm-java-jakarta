-- ============================================================================================================
-- wallet_db — Wallet Service'in kendi veritabanı
-- Container: java-town-mysql-wallet
-- Veritabanı ve kullanıcı (wallet_user) zaten docker-compose ortam
-- değişkenleriyle otomatik oluşturuldu — burada sadece tablo + veri var.
-- Çalıştırma:
--   docker exec -i java-town-mysql-wallet mysql -u wallet_user -p'WalletSaga123!' wallet_db < wallet_db.sql
-- ============================================================================================================

CREATE TABLE customers (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name      VARCHAR(255)  NOT NULL,
    e_mail         VARCHAR(255)  NOT NULL UNIQUE,
    wallet_balance DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO customers (full_name, e_mail, wallet_balance) VALUES
    ('Fritz Molle', 'fritz.molle@lectures.com', 500.00),
    ('Hanna Smidt', 'hanna.smidt@lectures.com', 20.00),
    ('Lars Petirsen', 'lars.petirsen@lectures.com', 100.00);