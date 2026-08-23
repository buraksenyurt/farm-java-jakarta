package com.lectures.bookingauditservice.dto;

public record BookingAttemptRequest(Long eventId, Long customerId, int seatCount, String status, String failureReason) {

}
