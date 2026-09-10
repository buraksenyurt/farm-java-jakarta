package com.lectures.saga.eventservice.service;

public class EventNotFoundException extends NotFoundException {

    public EventNotFoundException(Long eventId) {
        super(eventId + " numaralı etkinlik bulunamadı.");
    }

}
