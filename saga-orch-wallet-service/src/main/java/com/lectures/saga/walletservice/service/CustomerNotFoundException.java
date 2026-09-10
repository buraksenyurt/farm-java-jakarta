package com.lectures.saga.walletservice.service;

public class CustomerNotFoundException extends NotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super(customerId + " numaralı müşteri bulunamadı.");
    }

}
