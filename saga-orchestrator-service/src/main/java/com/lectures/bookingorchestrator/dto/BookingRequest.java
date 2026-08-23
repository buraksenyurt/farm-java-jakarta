package com.lectures.bookingorchestrator.dto;

public record BookingRequest(Long eventId, Long customerId, int seatCount) {

}
