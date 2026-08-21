package com.lectures.eventticketing.repository;

import com.lectures.eventticketing.model.Event;

public interface EventRepository {

    Event findById(Long id);

    void reserveSeats(Event event, int seatCount);
}
