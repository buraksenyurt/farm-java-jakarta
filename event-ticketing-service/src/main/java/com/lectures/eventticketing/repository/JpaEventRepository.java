package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Event;
import com.lectures.eventticketing.service.EventNotFoundException;
import com.lectures.eventticketing.service.InsufficientCapacityException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaEventRepository implements EventRepository {
    
    @PersistenceContext(unitName = "eventTicketingPU")
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
    
}
