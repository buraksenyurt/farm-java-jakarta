package com.lectures.saga.walletservice.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends BusinessConflictException {

    public InsufficientBalanceException(Long customerId, BigDecimal required, BigDecimal available) {
        super(customerId + " nolu müşterinin bakiyesi yetersiz. Gereken " + required + ", mevcut " + available);
    }
}
