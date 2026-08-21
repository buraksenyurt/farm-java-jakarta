package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Customer;
import java.math.BigDecimal;

public interface CustomerRepository {

    Customer findById(Long id);

    void chargeWallet(Customer customer, BigDecimal amount);
}
