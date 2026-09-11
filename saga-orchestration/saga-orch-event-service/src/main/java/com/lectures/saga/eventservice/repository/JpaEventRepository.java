package com.lectures.saga.eventservice.repository;

import com.lectures.saga.eventservice.model.Event;
import com.lectures.saga.eventservice.exception.EventNotFoundException;
import com.lectures.saga.eventservice.exception.InsufficientCapacityException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaEventRepository implements EventRepository {

    @PersistenceContext(unitName = "eventSagaPU")
    private EntityManager entityManager;

    @Override
    public Event findById(Long id) {
        Event event = entityManager.find(Event.class, id);
        if (event == null) {
            throw new EventNotFoundException(id);
        }
        return event;
    }

    @Override
    public void reserveSeats(Event event, int seatCount) {
        if (event.getSeatsSold() + seatCount > event.getTotalSeats()) {
            throw new InsufficientCapacityException(event.getId(), seatCount);
        }
        event.setSeatsSold(event.getSeatsSold() + seatCount);
    }

    @Override
    public void releaseSeats(Event event, int seatCount) {
        /*
            Telafi çağrısı. Ancak gerçek üretim ortamı senaryolarında,
        aynı telafi isteği ağ tekrarı nedeniyle iki kez de gelebilir. Bu
        nedenle Idempotency-Key kontrolü yapmak gerekir. Sonradan ele alalım.
         */
        event.setSeatsSold(Math.max(0, event.getSeatsSold() - seatCount));
    }

}
