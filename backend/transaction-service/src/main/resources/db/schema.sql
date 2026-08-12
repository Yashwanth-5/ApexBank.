-- =========================================================
-- transaction-service database (own schema)
-- =========================================================
DROP DATABASE IF EXISTS apexbank_txn_db;
CREATE DATABASE apexbank_txn_db;
USE apexbank_txn_db;

CREATE TABLE payees (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_account_id         BIGINT NOT NULL,
    payee_name              VARCHAR(100) NOT NULL,
    payee_account_number       VARCHAR(20) NOT NULL,
    nickname               VARCHAR(50),
    created_at              DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_payee_per_owner UNIQUE (owner_account_id, payee_account_number)
);

CREATE TABLE transactions (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_id          VARCHAR(30) NOT NULL UNIQUE,

    from_account_id        BIGINT NOT NULL,
    from_account_number      VARCHAR(20) NOT NULL,
    to_account_id          BIGINT NOT NULL,
    to_account_number        VARCHAR(20) NOT NULL,

    mode                 VARCHAR(10) NOT NULL, -- NEFT, UPI
    amount               DECIMAL(15,2) NOT NULL,
    remarks              VARCHAR(255),
    upi_id_used            VARCHAR(50),

    status               VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',

    transaction_datetime      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_txn_from_account ON transactions(from_account_id);
CREATE INDEX idx_txn_to_account ON transactions(to_account_id);
