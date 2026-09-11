package com.lectures.saga.eventservice.repository;

import com.lectures.saga.eventservice.model.Event;

public interface EventRepository {

    Event findById(Long id);

    void reserveSeats(Event event, int seatCount);

    void releaseSeats(Event event, int seatCount);
}
