package com.lectures.eventticketing.service;

public class InsufficientCapacityException extends ConflictException {

    public InsufficientCapacityException(Long eventId, int requestedSeat) {
        super("Etkinlikte ( " + eventId + ") yeterli koltuk yok. İstenen koltuk adedi " + requestedSeat);
    }
}
