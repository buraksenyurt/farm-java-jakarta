package com.lectures.saga.walletservice.dto;

import java.math.BigDecimal;

public record ChargeRequest(BigDecimal amount) {
    
}
