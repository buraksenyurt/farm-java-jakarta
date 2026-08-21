package com.lectures.inventory.figures.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class Publisher {
    private static final Logger logger = LoggerFactory.getLogger(Publisher.class);
    private static final String EXCHANGE_NAME = "figure.exchange";
    private static final String ROUTING_KEY = "figure.stock.arrived";

    private Connection connection;
    private Channel channel;
    private Jsonb jsonb;

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            // Docker Compose ağı içinden çalışıyorsak "rabbitmq", host makineden çalışıyorsak "localhost"
            // Tabii bu bilgiler aslında Vault gibi bir dış ortamdan gelmeli
            factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "localhost"));
            factory.setPort(5672);
            factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USER", "guest"));
            factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASS", "guest"));

            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(5000);

            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

            jsonb = JsonbBuilder.create();
            logger.info("RabbitMQ bağlantısı kuruldu, Exchange tanımlandı: {}", EXCHANGE_NAME);
        } catch (Exception e) {
            logger.error("RabbitMQ bağlantısı kurulurken hata oluştu: ", e);
        }
    }

    /**
     * synchronized: bu bean @ApplicationScoped olduğu için tek bir Channel
     * eşzamanlı isteklerce paylaşılır. Channel nesnesi thread-safe olmadığından
     * publish çağrısını serileştiriyoruz. Yüksek trafikli senaryolarda bunun yerine
     * bir Channel havuzu mesela istek başına kısa ömürlü channel kullanılması
     * çok daha iyi olabilir.
     */
    public synchronized void publishStockArrival(Object event) {
        try {
            String jsonMessage = jsonb.toJson(event);
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null,
                    jsonMessage.getBytes(StandardCharsets.UTF_8));
            logger.info("Event gönderildi [{}]: {}", ROUTING_KEY, jsonMessage);
        } catch (JsonbException | IOException e) {
            logger.error("Event gönderilirken hata oluştu!", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) channel.close();
            if (connection != null && connection.isOpen()) connection.close();
            logger.info("RabbitMQ bağlantıları güvenli bir şekilde kapatıldı.");
        } catch (IOException | TimeoutException e) {
            logger.error("Hata oluştu: ", e);
        }
    }
}
