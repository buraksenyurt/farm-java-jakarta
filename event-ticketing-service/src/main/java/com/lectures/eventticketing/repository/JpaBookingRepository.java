package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Booking;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class JpaBookingRepository implements BookingRepository {

    @PersistenceContext(unitName = "eventTicketingPU")
    private EntityManager entityManager;

    @Override
    public Booking save(Booking booking) {
        entityManager.persist(booking);
        return booking;
    }
}
