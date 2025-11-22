# E-Wallet Payment Processing System

A microservices-based backend system for managing digital wallet accounts, processing payments, and handling notifications.

## Architecture Overview

The system consists of three Spring Boot microservices:

1. **Wallet Service** (Port 8081)
   - Manages wallet accounts and transactions
   - Handles balance validation and debit operations
   - Maintains wallet transaction ledger

2. **Payment Service** (Port 8082)
   - Orchestrates payment processing workflow
   - Validates payments and processes transactions
   - Creates settlements and publishes notification events
   - Uses Feign Client to communicate with Wallet Service
   - Publishes events to Kafka for notifications

3. **Notification Service** (Port 8083)
   - Consumes payment notification events from Kafka
   - Sends notifications to merchants (email/webhook)
   - Tracks notification status

## Technology Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Spring Cloud OpenFeign** (for inter-service communication)
- **Apache Kafka** (for asynchronous event processing)
- **Maven** (build tool)
- **Docker & Docker Compose** (containerization)
- **OpenAPI 3 / Swagger** (API documentation)

## Database Schema

The system uses H2 in-memory databases with the following main entities:

- `customers` - Customer information
- `wallet_accounts` - Wallet accounts (1:1 with customers)
- `merchants` - Merchant information
- `supported_currencies` - Supported currency codes
- `payments` - Payment records
- `wallet_transactions` - Transaction ledger entries
- `settlements` - Settlement records for merchants
- `notification_events` - Notification tracking
- `audit_logs` - Audit trail

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for containerized deployment)
- Kafka (if running locally without Docker)

## Running Locally with Maven

### 1. Start Kafka (if not using Docker)

```bash
# Using Docker for Kafka only
docker run -d --name kafka -p 9092:9092 apache/kafka:latest
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run Services

**Terminal 1 - Wallet Service:**
```bash
cd wallet-service
mvn spring-boot:run
```

**Terminal 2 - Payment Service:**
```bash
cd payment-service
mvn spring-boot:run
```

**Terminal 3 - Notification Service:**
```bash
cd notification-service
mvn spring-boot:run
```

## Running with Docker Compose

```bash
docker-compose up --build
```

This will start:
- Zookeeper and Kafka
- Wallet Service (port 8081)
- Payment Service (port 8082)
- Notification Service (port 8083)

## API Endpoints

### Wallet Service

- `GET /wallets/{customerId}` - Get wallet details by customer ID
- `POST /wallets/validate-balance` - Validate wallet balance and currency
- `POST /wallets/{walletId}/debit` - Debit wallet for a payment

### Payment Service

- `POST /payments/initiate` - Initiate and validate a new payment (merges initiate + validate)
- `POST /payments/{paymentId}/process` - Process a validated payment
- `GET /payments/{paymentId}/status` - Get payment status

### Notification Service

- `POST /notifications/payment-success` - Trigger payment success notification

## API Documentation

Swagger UI is available at:
- Wallet Service: http://localhost:8081/swagger-ui.html
- Payment Service: http://localhost:8082/swagger-ui.html
- Notification Service: http://localhost:8083/swagger-ui.html

## Example API Calls

### 1. Get Wallet Details

```bash
curl -X GET http://localhost:8081/wallets/1001
```

**Response:**
```json
{
  "customerId": 1001,
  "walletId": 5001,
  "currency": "USD",
  "balance": 250.50,
  "status": "ACTIVE"
}
```

### 2. Initiate Payment (includes validation)

```bash
curl -X POST http://localhost:8082/payments/initiate \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1001,
    "walletId": 5001,
    "merchantId": 2001,
    "productId": 7001,
    "quantity": 1
  }'
```

**Response:**
```json
{
  "paymentId": 1,
  "status": "VALIDATED"
}
```

Note: The initiate endpoint now automatically validates the payment (checks merchant, product, balance, and currency). The amount is calculated from product cost × quantity.

### 3. Process Payment

```bash
curl -X POST http://localhost:8082/payments/1/process \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": 5001,
    "confirm": true
  }'
```

**Response:**
```json
{
  "paymentId": 1,
  "status": "COMPLETED",
  "walletFeeAmount": 2.00
}
```

### 5. Get Payment Status

```bash
curl -X GET http://localhost:8082/payments/1/status
```

**Response:**
```json
{
  "paymentId": 1,
  "status": "COMPLETED",
  "amount": 149.99,
  "currency": "USD",
  "merchantId": 2001,
  "walletFeeAmount": 2.00,
  "netAmountToMerchant": 147.99,
  "initiatedAt": "2024-01-01T10:00:00",
  "completedAt": "2024-01-01T10:00:05"
}
```

## Payment Flow

1. **Initiation & Validation**: Client calls `/payments/initiate` with customerId, walletId, merchantId, productId, and quantity
   - Payment Service validates merchant and product
   - Calculates amount from product cost × quantity
   - Validates wallet balance and currency via Wallet Service
   - Creates payment with VALIDATED status
2. **Processing**: Client calls `/payments/{paymentId}/process`
   - Wallet is debited via Wallet Service
   - Settlement record is created
   - Payment notification event is published to Kafka
3. **Notification**: Notification Service consumes the event and sends email notifications to merchant

## Kafka Topics

- **payment-notifications**: Topic for payment success events published by Payment Service and consumed by Notification Service

## Initial Data

The system is pre-populated with:
- 3 supported currencies: INR, USD, EUR
- 2 customers: Alice Johnson (ID: 1001), Bob Smith (ID: 1002)
- 2 wallets: One for each customer with initial balances
- 2 merchants: Tech Store (ID: 2001), Food Plaza (ID: 2002)
- 4 products:
  - Headphones (ID: 7001, cost: $149.99, merchant: Tech Store)
  - Laptop (ID: 7002, cost: $999.99, merchant: Tech Store)
  - Pizza (ID: 7003, cost: $12.99, merchant: Food Plaza)
  - Burger (ID: 7004, cost: $8.99, merchant: Food Plaza)

## Business Rules

1. **1:1 Customer-Wallet Relationship**: Each customer has exactly one wallet account
2. **Product-based Payments**: Payments are made for products with quantity. Amount = Product Cost × Quantity
3. **Wallet Fee**: 2% of payment amount is charged as wallet fee
4. **Settlement**: Net amount to merchant = Gross amount - Wallet fee
5. **Payment Status Flow**: VALIDATED (after initiate) → COMPLETED (or FAILED)
6. **Settlement Status Flow**: PENDING → IN_PROGRESS → COMPLETED (or FAILED)
7. **Notification**: Only email notifications are sent to merchants (no webhooks or SMS)

## Error Handling

All services include global exception handlers that return consistent JSON error responses. Validation errors are returned with HTTP 400, not found errors with HTTP 404, and server errors with HTTP 500.

## Logging

All services use SLF4J/Logback for logging. Log levels can be configured in `application.yml` files.

## Testing

Unit tests and integration tests can be added to each service. Run tests with:

```bash
mvn test
```

## H2 Console

H2 database console is available at:
- Wallet Service: http://localhost:8081/h2-console
- Payment Service: http://localhost:8082/h2-console
- Notification Service: http://localhost:8083/h2-console

JDBC URL: `jdbc:h2:mem:walletdb` (or `paymentdb`, `notificationdb`)
Username: `sa`
Password: (empty)

## Notes

- **No Security**: This system does not include authentication/authorization as per requirements
- **H2 In-Memory**: Databases are in-memory and data is lost on restart
- **Kafka**: Ensure Kafka is running before starting Payment and Notification services
- **Service Dependencies**: Payment Service depends on Wallet Service; ensure Wallet Service is running first

## Troubleshooting

1. **Kafka Connection Errors**: Ensure Kafka is running and accessible at `localhost:9092`
2. **Feign Client Errors**: Ensure Wallet Service is running before Payment Service
3. **Port Conflicts**: Change ports in `application.yml` if ports 8081-8083 are in use
4. **Database Errors**: Check H2 console to verify schema and data

## License

This is a demonstration project for educational purposes.

