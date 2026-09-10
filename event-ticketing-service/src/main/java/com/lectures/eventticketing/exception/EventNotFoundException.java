package com.lectures.eventticketing.exception;

public class EventNotFoundException extends ResourceNotFoundException {

    public EventNotFoundException(Long eventId) {
        super(eventId + " numaralı etkinlik bulunamadı.");
    }

}
