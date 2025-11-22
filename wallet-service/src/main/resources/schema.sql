-- E-Wallet System Database Schema
-- H2 Database

-- Supported Currencies
CREATE TABLE IF NOT EXISTS supported_currencies (
    code CHAR(3) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Customers
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(30),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Wallet Accounts (1:1 with customers)
CREATE TABLE IF NOT EXISTS wallet_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    wallet_number VARCHAR(50) UNIQUE NOT NULL,
    currency CHAR(3) NOT NULL,
    balance_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (currency) REFERENCES supported_currencies(code),
    CONSTRAINT unique_customer_wallet UNIQUE (customer_id)
);

-- Merchants
CREATE TABLE IF NOT EXISTS merchants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    merchant_code VARCHAR(50) UNIQUE NOT NULL,
    notification_email VARCHAR(255),
    notification_webhook VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_account_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    product_code VARCHAR(100),
    product_name VARCHAR(255) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    wallet_fee_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'INITIATED',
    failure_reason VARCHAR(255),
    initiated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    correlation_id VARCHAR(100),
    FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (currency) REFERENCES supported_currencies(code)
);

-- Wallet Transactions (Ledger)
CREATE TABLE IF NOT EXISTS wallet_transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_account_id BIGINT NOT NULL,
    payment_id BIGINT,
    entry_type VARCHAR(20) NOT NULL,
    purpose VARCHAR(30) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    balance_before DECIMAL(18,2) NOT NULL,
    balance_after DECIMAL(18,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts(id),
    FOREIGN KEY (payment_id) REFERENCES payments(id)
);

-- Settlements
CREATE TABLE IF NOT EXISTS settlements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    gross_amount DECIMAL(18,2) NOT NULL,
    wallet_fee_amount DECIMAL(18,2) NOT NULL,
    net_amount DECIMAL(18,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    gateway_reference VARCHAR(100),
    settlement_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

-- Notification Events
CREATE TABLE IF NOT EXISTS notification_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT,
    merchant_id BIGINT,
    wallet_account_id BIGINT,
    channel VARCHAR(30) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    payload TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (merchant_id) REFERENCES merchants(id),
    FOREIGN KEY (wallet_account_id) REFERENCES wallet_accounts(id)
);

-- Audit Logs
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    message VARCHAR(255),
    old_value TEXT,
    new_value TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

