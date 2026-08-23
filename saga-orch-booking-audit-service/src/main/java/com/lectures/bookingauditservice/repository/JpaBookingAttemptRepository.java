package com.lectures.bookingauditservice.repository;

import com.lectures.bookingauditservice.model.BookingAttempt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class JpaBookingAttemptRepository implements BookingAttemptRepository {

    @PersistenceContext(unitName = "bookingAuditSagaPU")
    private EntityManager em;

    @Override
    public void save(BookingAttempt attempt) {
        em.persist(attempt);
    }

    @Override
    public List<BookingAttempt> findAll() {
        return em.createQuery(
                "SELECT b FROM BookingAttempt b ORDER BY b.attemptedAt DESC", BookingAttempt.class)
                .getResultList();
    }
}
