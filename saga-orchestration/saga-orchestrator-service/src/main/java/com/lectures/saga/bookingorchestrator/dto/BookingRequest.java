package com.lectures.saga.bookingorchestrator.dto;

public record BookingRequest(Long eventId, Long customerId, int seatCount) {

}
