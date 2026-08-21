package com.lectures.cdi.payment;

import jakarta.enterprise.context.RequestScoped;

// .NET tarafındaki AddScoped karşılığı gibi düşünebiliriz
// Yani her HTTP isteğinden yeniden üretilir
@RequestScoped
public class CreditCardProcessor implements PaymentProcessor {

    public CreditCardProcessor() {
        System.out.println("CreditCardProcessor nesnesi oluşturuldu.");
    }

    @Override
    public String pay(double amount) {
        return amount + " TL ödeme kredi kartı ile yapıldı";
    }

}
