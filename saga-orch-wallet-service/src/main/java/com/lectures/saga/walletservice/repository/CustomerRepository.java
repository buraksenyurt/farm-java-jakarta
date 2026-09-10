package com.lectures.saga.walletservice.repository;

import com.lectures.saga.walletservice.model.Customer;
import java.math.BigDecimal;

public interface CustomerRepository {

    Customer findById(Long id);

    void charge(Customer customer, BigDecimal amount);

    void refund(Customer customer, BigDecimal amount);
}
