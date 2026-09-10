package com.lectures.saga.bookingorchestrator.client;

import com.lectures.saga.bookingorchestrator.dto.SeatRequest;
import com.lectures.saga.bookingorchestrator.service.SagaStepException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

@ApplicationScoped
public class EventServiceClient {

    private static final String BASE_URL = System.getenv().getOrDefault("EVENT_SERVICE_URL", "http://localhost:8081/event-service/api");

    private Client client;

    @PostConstruct
    void init() {
        client = ClientBuilder.newClient();
    }

    @PreDestroy
    void cleanup() {
        if (client != null) {
            client.close();
        }
    }

    public BigDecimal reserveSeats(Long eventId, int seatCount) {
        try (Response response = client.target(BASE_URL)
                .path("events/{id}/reserve-seats")
                .resolveTemplate("id", eventId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new SeatRequest(seatCount)))) {
            if (response.getStatus() == 200) {
                return response.readEntity(EventDto.class).ticketPrice();
            }
            throw new SagaStepException("Koltuk ayırma işlemi başarısız" + response.readEntity(String.class));

        }
    }

    public void releaseSeats(Long eventId, int seatCount) {
        try (Response response = client.target(BASE_URL)
                .path("events/{id}/release-seats")
                .resolveTemplate("id", eventId)
                .request(MediaType.APPLICATION_JSON).post(Entity.json(new SeatRequest(seatCount)))) {

        }
    }

    public record EventDto(Long id, String name, int totalSeats, int seatsSold, BigDecimal ticketPrice) {}
}
