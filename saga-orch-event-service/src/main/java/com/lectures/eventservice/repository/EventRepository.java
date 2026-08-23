package com.lectures.eventservice.repository;

import com.lectures.eventservice.model.Event;

public interface EventRepository {

    Event findById(Long id);

    void reserveSeats(Event event, int seatCount);

    void releaseSeats(Event event, int seatCount);
}
