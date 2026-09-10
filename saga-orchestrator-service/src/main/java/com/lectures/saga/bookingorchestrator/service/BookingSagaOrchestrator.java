package com.lectures.saga.bookingorchestrator.service;

import com.lectures.saga.bookingorchestrator.client.AuditServiceClient;
import com.lectures.saga.bookingorchestrator.client.EventServiceClient;
import com.lectures.saga.bookingorchestrator.client.WalletServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;

@ApplicationScoped
public class BookingSagaOrchestrator {

    @Inject
    private EventServiceClient eventServiceClient;

    @Inject
    private WalletServiceClient walletServiceClient;

    @Inject
    private AuditServiceClient auditServiceClient;

    public BookingResult bookTickets(Long eventId, Long customerId, int seatCount) {
        BigDecimal ticketPrice = BigDecimal.ZERO;

        try {
            ticketPrice = eventServiceClient.reserveSeats(eventId, seatCount);
        } catch (SagaStepException e) {
            auditServiceClient.logAttempt(eventId, customerId, seatCount, "FAILURE", e.getMessage());
        }

        BigDecimal totalPrice = ticketPrice.multiply(BigDecimal.valueOf(seatCount));

        try {
            walletServiceClient.charge(customerId, totalPrice);
        } catch (SagaStepException e) {
            eventServiceClient.releaseSeats(eventId, seatCount);
            auditServiceClient.logAttempt(eventId, customerId, seatCount, "FAILURE", e.getMessage());
        }

        auditServiceClient.logAttempt(eventId, customerId, seatCount, "SUCCESS", null);
        return new BookingResult(eventId, customerId, seatCount, totalPrice);
    }

    public record BookingResult(Long eventId, Long customerId, int seatCount, BigDecimal totalPrice) {}
}
