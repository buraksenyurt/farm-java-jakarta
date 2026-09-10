/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.lectures.saga.bookingorchestrator.client;

import com.lectures.saga.bookingorchestrator.dto.ChargeRequest;
import com.lectures.saga.bookingorchestrator.exception.SagaStepException;
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
public class WalletServiceClient {

    private static final String BASE_URL = System.getenv().getOrDefault("WALLET_SERVICE_URL", "http://localhost:8082/wallet-service/api");

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

    public void charge(Long customerId, BigDecimal amount) {
        try (Response response = client.target(BASE_URL)
                .path("customers/{id}/charge")
                .resolveTemplate("id", customerId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new ChargeRequest(amount)))) {
            if (response.getStatus() != 200) {
                throw new SagaStepException("Wallet servis ücreti alamadı" + response.readEntity(String.class));
            }
        }
    }

    public void refund(Long customerId, BigDecimal amount) {
        try (Response response = client.target(BASE_URL)
                .path("customers/{id}/refund")
                .resolveTemplate("id", customerId)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new ChargeRequest(amount)))) {
            // Log bırakabiliriz
        }
    }
}
