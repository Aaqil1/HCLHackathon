-- Initial Data for E-Wallet System

-- Supported Currencies
INSERT INTO supported_currencies (code, name, is_active) VALUES
('INR', 'Indian Rupee', TRUE),
('USD', 'US Dollar', TRUE),
('EUR', 'Euro', TRUE);

-- Customers
INSERT INTO customers (id, full_name, email, phone_number, status) VALUES
(1001, 'Alice Johnson', 'alice.johnson@example.com', '+1-555-0101', 'ACTIVE'),
(1002, 'Bob Smith', 'bob.smith@example.com', '+1-555-0102', 'ACTIVE');

-- Wallet Accounts (one per customer)
INSERT INTO wallet_accounts (id, customer_id, wallet_number, currency, balance_amount, status) VALUES
(5001, 1001, 'WALLET-ALICE-001', 'USD', 10000.50, 'ACTIVE'),
(5002, 1002, 'WALLET-BOB-001', 'USD', 5000.00, 'ACTIVE');

-- Merchants
INSERT INTO merchants (id, name, merchant_code, notification_email, notification_webhook, status) VALUES
(2001, 'Tech Store', 'TECH-STORE-001', 'notifications@techstore.com', 'https://api.techstore.com/webhooks/payment', 'ACTIVE'),
(2002, 'Food Plaza', 'FOOD-PLAZA-001', 'notifications@foodplaza.com', 'https://api.foodplaza.com/webhooks/payment', 'ACTIVE');

