package com.lectures.walletservice.repository;

import com.lectures.walletservice.model.Customer;
import java.math.BigDecimal;

public interface CustomerRepository {

    Customer findById(Long id);

    void charge(Customer customer, BigDecimal amount);

    void refund(Customer customer, BigDecimal amount);
}
