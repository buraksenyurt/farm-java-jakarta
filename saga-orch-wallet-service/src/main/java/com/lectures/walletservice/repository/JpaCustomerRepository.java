package com.lectures.walletservice.repository;

import com.lectures.walletservice.model.Customer;
import com.lectures.walletservice.service.CustomerNotFoundException;
import com.lectures.walletservice.service.InsufficientBalanceException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@ApplicationScoped
public class JpaCustomerRepository implements CustomerRepository {
    
    @PersistenceContext(unitName = "walletSagaPU")
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
    public void charge(Customer customer, BigDecimal amount) {
        if (customer.getWalletBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(customer.getId(), amount, customer.getWalletBalance());
        }
        customer.setWalletBalance(customer.getWalletBalance().subtract(amount));
    }
    
    @Override
    public void refund(Customer customer, BigDecimal amount) {
        customer.setWalletBalance(customer.getWalletBalance().add(amount));
    }
    
}
