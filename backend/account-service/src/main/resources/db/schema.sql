-- =========================================================
-- account-service database (own schema)
-- =========================================================
DROP DATABASE IF EXISTS apexbank_account_db;
CREATE DATABASE apexbank_account_db;
USE apexbank_account_db;

CREATE TABLE account_opening_requests (
    id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                   VARCHAR(10)  NOT NULL,
    first_name              VARCHAR(50)  NOT NULL,
    middle_name             VARCHAR(50),
    last_name               VARCHAR(50)  NOT NULL,
    father_name             VARCHAR(100) NOT NULL,
    mobile_number           VARCHAR(15)  NOT NULL,
    email                   VARCHAR(100),
    aadhar_number           VARCHAR(12)  NOT NULL,
    date_of_birth           DATE         NOT NULL,

    residential_address_line1  VARCHAR(150) NOT NULL,
    residential_address_line2  VARCHAR(150),
    residential_landmark       VARCHAR(100),
    residential_state          VARCHAR(50)  NOT NULL,
    residential_city           VARCHAR(50)  NOT NULL,
    residential_pincode         VARCHAR(10)  NOT NULL,

    permanent_address_line1    VARCHAR(150) NOT NULL,
    permanent_address_line2    VARCHAR(150),
    permanent_landmark         VARCHAR(100),
    permanent_state            VARCHAR(50)  NOT NULL,
    permanent_city             VARCHAR(50)  NOT NULL,
    permanent_pincode           VARCHAR(10)  NOT NULL,

    occupation_type          VARCHAR(50)  NOT NULL,
    source_of_income          VARCHAR(50)  NOT NULL,
    gross_annual_income        VARCHAR(50)  NOT NULL,

    wants_debit_card         BOOLEAN DEFAULT FALSE,
    opt_for_net_banking       BOOLEAN DEFAULT FALSE,

    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    rejection_reason          VARCHAR(255),

    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number       VARCHAR(20) NOT NULL UNIQUE,
    request_id           BIGINT,

    title               VARCHAR(10)  NOT NULL,
    first_name           VARCHAR(50)  NOT NULL,
    middle_name          VARCHAR(50),
    last_name            VARCHAR(50)  NOT NULL,
    mobile_number        VARCHAR(15)  NOT NULL,
    email                VARCHAR(100),
    aadhar_number        VARCHAR(12)  NOT NULL,
    date_of_birth        DATE         NOT NULL,

    residential_address_line1  VARCHAR(150) NOT NULL,
    residential_address_line2  VARCHAR(150),
    residential_landmark       VARCHAR(100),
    residential_state          VARCHAR(50)  NOT NULL,
    residential_city           VARCHAR(50)  NOT NULL,
    residential_pincode         VARCHAR(10)  NOT NULL,

    permanent_address_line1    VARCHAR(150) NOT NULL,
    permanent_address_line2    VARCHAR(150),
    permanent_landmark         VARCHAR(100),
    permanent_state            VARCHAR(50)  NOT NULL,
    permanent_city             VARCHAR(50)  NOT NULL,
    permanent_pincode           VARCHAR(10)  NOT NULL,

    occupation_type       VARCHAR(50) NOT NULL,
    account_type          VARCHAR(20) NOT NULL DEFAULT 'SAVINGS',
    balance              DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    has_debit_card        BOOLEAN DEFAULT FALSE,
    net_banking_enabled     BOOLEAN DEFAULT FALSE,

    status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_account_request FOREIGN KEY (request_id) REFERENCES account_opening_requests(id)
);

CREATE TABLE upi_ids (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    upi_id        VARCHAR(50) NOT NULL UNIQUE,
    account_id     BIGINT NOT NULL UNIQUE,
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_upi_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);
