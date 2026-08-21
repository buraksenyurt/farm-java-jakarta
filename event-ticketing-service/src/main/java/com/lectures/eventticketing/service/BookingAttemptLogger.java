package com.lectures.eventticketing.service;

import com.lectures.eventticketing.model.BookingAttempt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;

/*
    Diğer repository sınıflarında Transactional anotasyonu kullanmadık zira
o repository nesneler transaction sınırında değiller. Bu nesneler orkestrasyon
yapan TicketBookingService' de Transaction içerisine dahil oluyorlar.

    Ancak bu sınıfta onlardan farklı olarak logAttempt metodunun bir transaction
gerektirdiğini belirtiyoruz; REQUIRES_NEW. Bu metot çağrıldığında container
mevcut transaction'ı askıya alır ve ondan bağımsız tamamen yeni bir transaction
başlatır. entityManager.persist() bu yeni transaction tarafından commit edilir.
Sonrasında çağrının yapıldığı ve duraksatılmış transaction'a geri dönülür.

    Burada amaç booking_attempts tablosuna her durumda(container transaction
tarafı patlasa dahi) kayıt atılmasını garanti etmektir. Dolayısıyla container
tarafındaki ana transaction rollback olsa bile burada ilgili duruma ait bilgiler
kayıt altına alınmış olur.

*/
@ApplicationScoped
public class BookingAttemptLogger {

    @PersistenceContext(unitName = "eventTicketingPU")
    private EntityManager entityManager;

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void logAttempt(Long eventId, Long customerId, int seatCount, String status, String failureReason) {
        BookingAttempt attempt = new BookingAttempt();

        attempt.setEventId(eventId);
        attempt.setCustomerId(customerId);
        attempt.setSeatCount(seatCount);
        attempt.setStatus(status);
        attempt.setFailureReason(failureReason);
        attempt.setAttemptTime(LocalDateTime.now());

        entityManager.persist(attempt);
    }
}
