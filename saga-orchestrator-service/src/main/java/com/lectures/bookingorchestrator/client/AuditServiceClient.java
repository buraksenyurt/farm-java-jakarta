package com.lectures.bookingorchestrator.client;

import com.lectures.bookingorchestrator.dto.BookingAttemptRequest;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class AuditServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceClient.class);

    private static final String BASE_URL = System.getenv().getOrDefault("AUDIT_SERVICE_URL", "http://localhost:8083/booking-audit-service/api");

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

    public void logAttempt(Long eventId, Long customerId, int seatCount, String status, String failureReason) {
        try (Response response = client.target(BASE_URL)
                .path("booking-attempts")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new BookingAttemptRequest(eventId, customerId, seatCount, status, failureReason)))) {

            if (response.getStatus() != 201) {
                logger.warn("Audit kaydı oluşturulamadı: HTTP {}", response.getStatus());
            }
        } catch (Exception e) {
            logger.warn("Audit Service'e ulaşılamadı, kayıt atlanıyor {}", e.getMessage());
        }
    }
}
