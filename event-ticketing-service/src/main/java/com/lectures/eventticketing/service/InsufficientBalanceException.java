package com.lectures.eventticketing.service;

import java.math.BigDecimal;

public class InsufficientBalanceException extends ConflictException {

    public InsufficientBalanceException(Long customerId, BigDecimal required, BigDecimal available) {
        super(customerId + " nolu müşterinin bakiyesi yetersiz. Gereken " + required + ", mevcut " + available);
    }
}
