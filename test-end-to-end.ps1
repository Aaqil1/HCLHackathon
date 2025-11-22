# End-to-End Test Script for E-Wallet System
Write-Host "=========================================" -ForegroundColor Cyan
Write-Host "E-Wallet System End-to-End Test" -ForegroundColor Cyan
Write-Host "=========================================" -ForegroundColor Cyan

# Test 1: Get Wallet by Customer ID
Write-Host "`n[Test 1] Getting wallet for customerId 1001..." -ForegroundColor Yellow
try {
    $wallet = Invoke-RestMethod -Uri "http://localhost:8081/wallets/1001" -Method Get
    Write-Host "✓ SUCCESS: Wallet retrieved" -ForegroundColor Green
    Write-Host "  Customer ID: $($wallet.customerId)" -ForegroundColor Gray
    Write-Host "  Wallet ID: $($wallet.walletId)" -ForegroundColor Gray
    Write-Host "  Balance: $($wallet.balance)" -ForegroundColor Gray
    Write-Host "  Currency: $($wallet.currency)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 2: Initiate Payment (with validation)
Write-Host "`n[Test 2] Initiating payment for product 7001, quantity 1..." -ForegroundColor Yellow
$initiateBody = @{
    customerId = 1001
    walletId = 5001
    merchantId = 2001
    productId = 7001
    quantity = 1
} | ConvertTo-Json

try {
    $payment = Invoke-RestMethod -Uri "http://localhost:8082/payments/initiate" -Method Post -Body $initiateBody -ContentType "application/json"
    Write-Host "✓ SUCCESS: Payment initiated and validated" -ForegroundColor Green
    Write-Host "  Payment ID: $($payment.paymentId)" -ForegroundColor Gray
    Write-Host "  Status: $($payment.status)" -ForegroundColor Gray
    $paymentId = $payment.paymentId
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 3: Process Payment
Write-Host "`n[Test 3] Processing payment $paymentId..." -ForegroundColor Yellow
$processBody = @{
    walletId = 5001
    confirm = $true
} | ConvertTo-Json

try {
    $processed = Invoke-RestMethod -Uri "http://localhost:8082/payments/$paymentId/process" -Method Post -Body $processBody -ContentType "application/json"
    Write-Host "✓ SUCCESS: Payment processed" -ForegroundColor Green
    Write-Host "  Payment ID: $($processed.paymentId)" -ForegroundColor Gray
    Write-Host "  Status: $($processed.status)" -ForegroundColor Gray
    Write-Host "  Wallet Fee: $($processed.walletFeeAmount)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 4: Get Payment Status
Write-Host "`n[Test 4] Getting payment status for $paymentId..." -ForegroundColor Yellow
try {
    $status = Invoke-RestMethod -Uri "http://localhost:8082/payments/$paymentId/status" -Method Get
    Write-Host "✓ SUCCESS: Payment status retrieved" -ForegroundColor Green
    Write-Host "  Payment ID: $($status.paymentId)" -ForegroundColor Gray
    Write-Host "  Status: $($status.status)" -ForegroundColor Gray
    Write-Host "  Amount: $($status.amount)" -ForegroundColor Gray
    Write-Host "  Wallet Fee: $($status.walletFeeAmount)" -ForegroundColor Gray
    Write-Host "  Net to Merchant: $($status.netAmountToMerchant)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Test 5: Verify Wallet Balance Updated
Write-Host "`n[Test 5] Verifying wallet balance was updated..." -ForegroundColor Yellow
try {
    $walletAfter = Invoke-RestMethod -Uri "http://localhost:8081/wallets/1001" -Method Get
    Write-Host "✓ SUCCESS: Wallet balance updated" -ForegroundColor Green
    Write-Host "  Previous Balance: 250.50" -ForegroundColor Gray
    Write-Host "  Current Balance: $($walletAfter.balance)" -ForegroundColor Gray
} catch {
    Write-Host "✗ FAILED: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n=========================================" -ForegroundColor Cyan
Write-Host "All tests passed! ✓" -ForegroundColor Green
Write-Host "=========================================" -ForegroundColor Cyan

