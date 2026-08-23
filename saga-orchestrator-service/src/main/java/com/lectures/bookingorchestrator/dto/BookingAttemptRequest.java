package com.lectures.bookingorchestrator.dto;

public record BookingAttemptRequest(Long eventId, Long customerId, int seatCount, String status, String failureReason) {

}
