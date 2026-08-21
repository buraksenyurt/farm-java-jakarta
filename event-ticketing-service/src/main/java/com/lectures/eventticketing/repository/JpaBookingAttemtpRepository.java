package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.BookingAttempt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@ApplicationScoped
public class JpaBookingAttemtpRepository implements BookingAttemptRepository {

    @PersistenceContext(unitName = "eventTicketingPU")
    private EntityManager entityManager;

    @Override
    public List<BookingAttempt> findAll() {
        return entityManager.createQuery("SELECT b FROM BookingAttempt b ORDER BY b.attemptTime DESC", BookingAttempt.class)
                .getResultList();
    }

}
