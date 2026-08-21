CREATE TABLE games (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(255)    NOT NULL,
    genre           VARCHAR(30)     NOT NULL,
    platform        VARCHAR(30)     NOT NULL,
    release_year    INT             NOT NULL,
    publisher       VARCHAR(255)    NOT NULL,
    score           INT             NOT NULL,
    price           DOUBLE          NOT NULL,
    stock_quantity  INT             NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
