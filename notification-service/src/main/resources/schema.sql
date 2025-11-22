-- E-Wallet System Database Schema
-- H2 Database (Notification Service)



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
    FOREIGN KEY (merchant_id) REFERENCES merchants(id)
);

