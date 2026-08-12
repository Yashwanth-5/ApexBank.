-- =========================================================
-- auth-service database (own schema, per microservices principles)
-- =========================================================
DROP DATABASE IF EXISTS apexbank_auth_db;
CREATE DATABASE apexbank_auth_db;
USE apexbank_auth_db;

CREATE TABLE users (
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id                 VARCHAR(30) NOT NULL UNIQUE,
    account_id               BIGINT NOT NULL UNIQUE,
    account_number            VARCHAR(20) NOT NULL,
    full_name                VARCHAR(150) NOT NULL,

    login_password_hash        VARCHAR(255) NOT NULL,
    transaction_password_hash    VARCHAR(255) NOT NULL,

    role                   VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',

    failed_login_attempts       INT NOT NULL DEFAULT 0,
    account_locked            BOOLEAN NOT NULL DEFAULT FALSE,

    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE otp_store (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier      VARCHAR(50) NOT NULL,
    otp_code       VARCHAR(10) NOT NULL,
    purpose        VARCHAR(30) NOT NULL,
    expires_at      DATETIME NOT NULL,
    used          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_otp_identifier ON otp_store(identifier);
