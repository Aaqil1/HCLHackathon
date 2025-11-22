# Wallet Service APIs - Complete Documentation & Demo Guide

## Overview
The **Wallet Service** (Port 8081) manages wallet accounts, balance validation, and debit operations. It exposes **3 REST APIs** for wallet management.

---

## API 1: Get Wallet Details by Customer ID

### Endpoint
```
GET /wallets/{customerId}
```

### Description
Retrieves wallet account details for a customer, including balance, currency, and status.

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `customerId` | Long | Yes | Customer ID (e.g., 1001) |

### Request Example
```bash
GET http://localhost:8081/wallets/1001
```

### Success Response (200 OK)
```json
{
  "customerId": 1001,
  "walletId": 5001,
  "currency": "USD",
  "balance": 250.50,
  "status": "ACTIVE"
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| `customerId` | Long | Customer ID |
| `walletId` | Long | Wallet account ID |
| `currency` | String | Currency code (USD, INR, EUR) |
| `balance` | BigDecimal | Current wallet balance |
| `status` | String | Wallet status (ACTIVE, BLOCKED, CLOSED) |

### Error Responses
- **404 Not Found**: Customer or wallet not found
- **500 Internal Server Error**: Server error

### Explanation
1. Validates customer exists and is ACTIVE
2. Fetches wallet by `customerId` (1:1 relationship)
3. Returns wallet details with current balance

### Demo Scenarios

#### Scenario 1: Get Alice's Wallet
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

#### Scenario 2: Get Bob's Wallet
```bash
curl -X GET http://localhost:8081/wallets/1002
```

**Response:**
```json
{
  "customerId": 1002,
  "walletId": 5002,
  "currency": "USD",
  "balance": 500.00,
  "status": "ACTIVE"
}
```

---

## API 2: Validate Wallet Balance

### Endpoint
```
POST /wallets/validate-balance
```

### Description
Validates if a wallet has sufficient balance and supports the requested currency before processing a payment.

### Request Body
```json
{
  "walletId": 5001,
  "amount": 149.99,
  "currency": "USD",
  "paymentId": 1
}
```

### Request Fields
| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `walletId` | Long | Yes | Not null | Wallet account ID |
| `amount` | BigDecimal | Yes | Not null, Positive | Amount to validate |
| `currency` | String | Yes | Not null | Currency code (3 chars) |
| `paymentId` | Long | No | - | Optional payment ID for tracking |

### Success Response (200 OK)
```json
{
  "status": "VALID",
  "hasSufficientBalance": true,
  "isCurrencySupported": true
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| `status` | String | "VALID" or "INVALID" |
| `hasSufficientBalance` | Boolean | true if balance >= amount |
| `isCurrencySupported` | Boolean | true if currency is active and matches wallet currency |

### Error Responses
- **400 Bad Request**: Invalid request body or validation errors

### Explanation
1. Checks wallet exists
2. Validates currency:
   - Currency is active in `supported_currencies`
   - Currency matches wallet currency
3. Validates balance:
   - Compares `wallet.balanceAmount >= amount`
4. Returns validation result

### Demo Scenarios

#### Scenario 1: Valid Balance Check
```bash
curl -X POST http://localhost:8081/wallets/validate-balance \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": 5001,
    "amount": 149.99,
    "currency": "USD",
    "paymentId": 1
  }'
```

**Response:**
```json
{
  "status": "VALID",
  "hasSufficientBalance": true,
  "isCurrencySupported": true
}
```
**Reason:** Wallet has $250.50, amount is $149.99, currency matches USD.

#### Scenario 2: Insufficient Balance
```bash
curl -X POST http://localhost:8081/wallets/validate-balance \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": 5001,
    "amount": 500.00,
    "currency": "USD"
  }'
```

**Response:**
```json
{
  "status": "INVALID",
  "hasSufficientBalance": false,
  "isCurrencySupported": true
}
```
**Reason:** Wallet has $250.50, amount is $500.00 (insufficient).

#### Scenario 3: Currency Mismatch
```bash
curl -X POST http://localhost:8081/wallets/validate-balance \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": 5001,
    "amount": 100.00,
    "currency": "EUR"
  }'
```

**Response:**
```json
{
  "status": "INVALID",
  "hasSufficientBalance": true,
  "isCurrencySupported": false
}
```
**Reason:** Wallet currency is USD, request currency is EUR (mismatch).

#### Scenario 4: Wallet Not Found
```bash
curl -X POST http://localhost:8081/wallets/validate-balance \
  -H "Content-Type: application/json" \
  -d '{
    "walletId": 9999,
    "amount": 100.00,
    "currency": "USD"
  }'
```

**Response:**
```json
{
  "status": "INVALID",
  "hasSufficientBalance": false,
  "isCurrencySupported": false
}
```
**Reason:** Wallet ID 9999 does not exist.

---

## API 3: Debit Wallet

### Endpoint
```
POST /wallets/{walletId}/debit
```

### Description
Debits an amount from a wallet account for a payment. Uses pessimistic locking to prevent concurrent updates.

### Path Parameters
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `walletId` | Long | Yes | Wallet account ID to debit |

### Request Body
```json
{
  "paymentId": 1,
  "amount": 149.99,
  "currency": "USD"
}
```

### Request Fields
| Field | Type | Required | Validation | Description |
|-------|------|----------|------------|-------------|
| `paymentId` | Long | Yes | Not null | Payment ID for tracking |
| `amount` | BigDecimal | Yes | Not null, Positive | Amount to debit |
| `currency` | String | Yes | Not null | Currency code (must match wallet) |

### Success Response (200 OK)
```json
{
  "status": "DEBIT_SUCCESS",
  "balanceAfter": 100.51
}
```

### Response Fields
| Field | Type | Description |
|-------|------|-------------|
| `status` | String | "DEBIT_SUCCESS" on success |
| `balanceAfter` | BigDecimal | Wallet balance after debit |

### Error Responses
- **404 Not Found**: Wallet not found or inactive
- **409 Conflict**: Insufficient balance or currency mismatch
- **400 Bad Request**: Invalid request body

### Explanation
1. **Locks wallet** using pessimistic lock (`@Lock(LockModeType.PESSIMISTIC_WRITE)`)
2. Validates wallet is ACTIVE
3. Validates currency matches wallet currency
4. Validates sufficient balance
5. **Deducts amount**: `balanceAfter = balanceBefore - amount`
6. Updates wallet balance in database
7. Creates ledger entry in `wallet_transactions`:
   - Entry Type: `DEBIT`
   - Purpose: `PAYMENT`
   - Records `balance_before` and `balance_after`
8. Returns new balance

### Demo Scenarios

#### Scenario 1: Successful Debit
```bash
curl -X POST http://localhost:8081/wallets/5001/debit \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "amount": 149.99,
    "currency": "USD"
  }'
```

**Response:**
```json
{
  "status": "DEBIT_SUCCESS",
  "balanceAfter": 100.51
}
```

**Calculation:**
- Balance Before: $250.50
- Amount Debited: $149.99
- Balance After: $250.50 - $149.99 = $100.51

**Database Changes:**
- `wallet_accounts.balance_amount`: 250.50 → 100.51
- New row in `wallet_transactions`:
  ```sql
  wallet_account_id: 5001
  payment_id: 1
  entry_type: 'DEBIT'
  purpose: 'PAYMENT'
  amount: 149.99
  balance_before: 250.50
  balance_after: 100.51
  ```

#### Scenario 2: Insufficient Balance
```bash
curl -X POST http://localhost:8081/wallets/5001/debit \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 2,
    "amount": 500.00,
    "currency": "USD"
  }'
```

**Response:** 409 Conflict
```json
{
  "error": "Insufficient balance. Available: 250.50, Required: 500.00"
}
```

#### Scenario 3: Currency Mismatch
```bash
curl -X POST http://localhost:8081/wallets/5001/debit \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 3,
    "amount": 100.00,
    "currency": "EUR"
  }'
```

**Response:** 409 Conflict
```json
{
  "error": "Currency mismatch. Wallet currency: USD"
}
```

#### Scenario 4: Wallet Not Found
```bash
curl -X POST http://localhost:8081/wallets/9999/debit \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 4,
    "amount": 100.00,
    "currency": "USD"
  }'
```

**Response:** 404 Not Found

---

## Complete Demo Flow

### Step 1: Check Initial Balance
```bash
GET http://localhost:8081/wallets/1001
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

### Step 2: Validate Balance Before Payment
```bash
POST http://localhost:8081/wallets/validate-balance
{
  "walletId": 5001,
  "amount": 149.99,
  "currency": "USD",
  "paymentId": 1
}
```
**Response:**
```json
{
  "status": "VALID",
  "hasSufficientBalance": true,
  "isCurrencySupported": true
}
```

### Step 3: Debit Wallet for Payment
```bash
POST http://localhost:8081/wallets/5001/debit
{
  "paymentId": 1,
  "amount": 149.99,
  "currency": "USD"
}
```
**Response:**
```json
{
  "status": "DEBIT_SUCCESS",
  "balanceAfter": 100.51
}
```

### Step 4: Verify Balance After Debit
```bash
GET http://localhost:8081/wallets/1001
```
**Response:**
```json
{
  "customerId": 1001,
  "walletId": 5001,
  "currency": "USD",
  "balance": 100.51,
  "status": "ACTIVE"
}
```

---

## Initial Test Data

From `data.sql`:

### Customers
- **Customer ID: 1001** (Alice Johnson)
- **Customer ID: 1002** (Bob Smith)

### Wallets
- **Wallet ID: 5001** (Customer: 1001, Balance: $250.50, Currency: USD)
- **Wallet ID: 5002** (Customer: 1002, Balance: $500.00, Currency: USD)

### Supported Currencies
- **USD** (US Dollar) - Active
- **INR** (Indian Rupee) - Active
- **EUR** (Euro) - Active

---

## Technical Details

### Pessimistic Locking
The debit operation uses **pessimistic locking** to prevent race conditions:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<WalletAccount> findByIdWithLock(Long id);
```
This ensures only one transaction can debit a wallet at a time.

### Transaction Management
The debit operation is wrapped in `@Transactional` to ensure atomicity:
- If any step fails, the entire operation is rolled back
- Balance update and ledger entry creation happen together

### Ledger Entries
Every debit creates an **immutable ledger entry** in `wallet_transactions`:
- Records balance before and after
- Links to payment via `payment_id`
- Used for audit and reconciliation

---

## Postman Collection

You can import these requests into Postman:

### Collection: Wallet Service APIs

1. **Get Wallet by Customer ID**
   - Method: GET
   - URL: `http://localhost:8081/wallets/1001`

2. **Validate Balance**
   - Method: POST
   - URL: `http://localhost:8081/wallets/validate-balance`
   - Body (JSON):
     ```json
     {
       "walletId": 5001,
       "amount": 149.99,
       "currency": "USD",
       "paymentId": 1
     }
     ```

3. **Debit Wallet**
   - Method: POST
   - URL: `http://localhost:8081/wallets/5001/debit`
   - Body (JSON):
     ```json
     {
       "paymentId": 1,
       "amount": 149.99,
       "currency": "USD"
     }
     ```

---

## Summary

The Wallet Service provides:
1. ✅ **Wallet Retrieval** - Get wallet details by customer ID
2. ✅ **Balance Validation** - Validate balance and currency before payments
3. ✅ **Secure Debit Operations** - Debit wallet with locking and transaction management

All operations are logged and create audit trails for compliance and debugging.

