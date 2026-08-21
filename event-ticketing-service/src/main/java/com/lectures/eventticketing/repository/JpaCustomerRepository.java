package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Customer;
import com.lectures.eventticketing.service.CustomerNotFoundException;
import com.lectures.eventticketing.service.InsufficientBallanceException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@ApplicationScoped
public class JpaCustomerRepository implements CustomerRepository {

    @PersistenceContext(unitName = "eventTicketingPU")
    private EntityManager entityManager;

    @Override
    public Customer findById(Long id) {
        Customer customer = entityManager.find(Customer.class, id);
        if (customer == null) {
            throw new CustomerNotFoundException(id);
        }
        return customer;
    }

    @Override
    public void chargeWallet(Customer customer, BigDecimal amount) {
        if (customer.getWalletBalance().compareTo(amount) < 0) {
            throw new InsufficientBallanceException(customer.getId(), amount, customer.getWalletBalance());
        }
        customer.setWalletBalance(customer.getWalletBalance().subtract(amount));
    }

}
