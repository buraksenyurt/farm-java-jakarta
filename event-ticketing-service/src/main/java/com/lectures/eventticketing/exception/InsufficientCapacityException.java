package com.lectures.eventticketing.exception;

public class InsufficientCapacityException extends BusinessConflictException {

    public InsufficientCapacityException(Long eventId, int requestedSeat) {
        super("Etkinlikte ( " + eventId + ") yeterli koltuk yok. İstenen koltuk adedi " + requestedSeat);
    }
}
