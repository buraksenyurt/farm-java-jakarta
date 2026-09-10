package com.lectures.eventticketing.exception;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Long customerId) {
        super(customerId + " numaralı müşteri bulunamadı.");
    }

}
