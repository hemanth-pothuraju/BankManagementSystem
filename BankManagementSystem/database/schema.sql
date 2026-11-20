-- Bank Management System : MySQL schema
CREATE DATABASE IF NOT EXISTS bank_management_system;
USE bank_management_system;

CREATE TABLE IF NOT EXISTS users (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    phone       VARCHAR(15),
    address     VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS accounts (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    account_number  VARCHAR(16) NOT NULL UNIQUE,
    account_type    ENUM('Savings','Current') NOT NULL,
    balance         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transactions (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    account_id        INT NOT NULL,
    txn_type          ENUM('DEPOSIT','WITHDRAW','TRANSFER_IN','TRANSFER_OUT') NOT NULL,
    amount            DECIMAL(15,2) NOT NULL,
    balance_after     DECIMAL(15,2) NOT NULL,
    related_account   VARCHAR(16),
    description       VARCHAR(255),
    txn_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_account FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);
