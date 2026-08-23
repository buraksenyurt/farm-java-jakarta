package com.lectures.walletservice.service;

import java.math.BigDecimal;

public class InsufficientBallanceException extends ConflictException {

    public InsufficientBallanceException(Long customerId, BigDecimal required, BigDecimal available) {
        super(customerId + " nolu müşterinin bakiyesi yetersiz. Gereken " + required + ", mevcut " + available);
    }
}
