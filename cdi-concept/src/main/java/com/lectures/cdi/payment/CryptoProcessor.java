package com.lectures.cdi.payment;

import jakarta.enterprise.context.ApplicationScoped;

// .NET tarafındaki AddSingleton karşılığı olarak düşünebiliriz.
// Sunucu çalıştığı sürece tek bir instance kullanılır
@ApplicationScoped
// Kendi yazdığımız Qualifier anotasyonunu burada kullandık
// Böylece DI tarafında aynı PaymentProcessor'dan türetme olsa bile anotasyon
// sayesinde doğru bileşeni kullandırabiliriz
@Crypto
public class CryptoProcessor implements PaymentProcessor {

    public CryptoProcessor() {
        System.out.println("CryptoProcessor nesnesi üretildi");
    }

    @Override
    public String pay(double amount) {
        return amount + " değerinde Kripto para ile ödeme yapıldı";
    }

}
