-- Initial Data for Notification Service (minimal - mostly populated via Kafka events)

-- Merchants (for reference)
INSERT INTO merchants (id, name, merchant_code, notification_email, notification_webhook, status) VALUES
(2001, 'Tech Store', 'TECH-STORE-001', 'notifications@techstore.com', 'https://api.techstore.com/webhooks/payment', 'ACTIVE'),
(2002, 'Food Plaza', 'FOOD-PLAZA-001', 'notifications@foodplaza.com', 'https://api.foodplaza.com/webhooks/payment', 'ACTIVE');

