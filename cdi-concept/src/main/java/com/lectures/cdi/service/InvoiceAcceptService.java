package com.lectures.cdi.service;

import com.lectures.cdi.payment.Crypto;
import com.lectures.cdi.payment.PaymentProcessor;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class InvoiceAcceptService {

    @Inject
    private PaymentProcessor paymentProcessor;

    // Dikkat edileceği üzere PaymentProcessor türünden de olsa 
    // @Crypto işaretlemesi nedeniyle CDI üzerinde @Crypto işareti olan
    // PaymentProcessor implementasyonunu kullanır.
    @Inject
    @Crypto
    private PaymentProcessor cryptoProcessor;

    public String procesStandard(double amount) {
        return paymentProcessor.pay(amount);
    }

    public String processCrypto(double amount) {
        return cryptoProcessor.pay(amount);
    }
}
