package com.lectures.inventory.notification.messaging;

import com.lectures.inventory.notification.model.Figure;
import com.lectures.inventory.notification.service.NotificationSender;
import com.rabbitmq.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private static final String EXCHANGE_NAME = "figure.exchange";
    private static final String ROUTING_KEY = "figure.stock.arrived";
    private static final String QUEUE_NAME = "notification.figure.stock.arrived.q";

    private static final String DLX_NAME = "figure.exchange.dlx";
    private static final String DLQ_NAME = "notification.figure.stock.arrived.dlq";

    @Inject
    private NotificationSender notificationSender;

    private Connection connection;
    private Channel channel;
    private Jsonb jsonb;

    // @ApplicationScoped bean'ler normalde tembel proxy'dir; hiçbir yerden
    // @Inject edilip metodu çağrılmadığı için instantiate edilmiyordu ve
    // @PostConstruct hiç çalışmıyordu. Bu observer, container ApplicationScoped
    // context'ini başlattığında (deploy anında) bean'in eager oluşturulmasını tetikler.
    public void onStartup(@Observes @Initialized(ApplicationScoped.class) Object init) {
    }

    @PostConstruct
    public void init() {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "localhost"));
            factory.setPort(5672);
            factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USER", "guest"));
            factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASS", "guest"));
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(5000);

            connection = factory.newConnection();
            channel = connection.createChannel();
            jsonb = JsonbBuilder.create();

            declareTopology();

            // QoS: broker'ın bize aynı anda en fazla kaç mesaj göndereceğini sınırlar.
            // Bu ayarlamaya yapmadığımızda broker ack bekletmeden tüm birikmiş mesajları tek seferde
            // pompalar ve yük altında bellek baskısına ve dengesiz dağılıma yol açabilir.
            channel.basicQos(10);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> handleDelivery(delivery);
            CancelCallback cancelCallback = tag -> logger.warn("Consumer iptal edildi: {}", tag);

            // autoAck = false: manuel onaylama, mesaj kaybını önlemek için
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, cancelCallback);

            logger.info("RabbitMQ consumer başlatıldı, kuyruk: {}", QUEUE_NAME);
        } catch (Exception e) {
            logger.error("RabbitMQ consumer başlatılırken hata oluştu: ", e);
        }
    }

    private void declareTopology() throws IOException {
        // Exchange'i producer zaten deklare ediyor; burada aynı parametrelerle
        // tekrar deklare etmek idempotent'tir (RabbitMQ, aynı özelliklerle
        // yapılan bu tip tekrarlı örneklemeleri hata saymaz). 
        // Buradaki iki servisten hangisi ilk önce ayağa kalkarsa kalksın 
        // topolojinin doğru kurulmasını garanti etmiş oluruz
        channel.exchangeDeclare(EXCHANGE_NAME, "topic", true);

        // Dead Letter Exchange + Queue: işlenemeyen mesajlar buraya düşer,
        // sessizce kaybolmaz ve ana kuyruğu tıkamaz.
        channel.exchangeDeclare(DLX_NAME, "fanout", true);
        channel.queueDeclare(DLQ_NAME, true, false, false, null);
        channel.queueBind(DLQ_NAME, DLX_NAME, "");

        Map<String, Object> queueArgs = new HashMap<>();
        queueArgs.put("x-dead-letter-exchange", DLX_NAME);

        channel.queueDeclare(QUEUE_NAME, true, false, false, queueArgs);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
    }

    private void handleDelivery(Delivery delivery) {
        long deliveryTag = delivery.getEnvelope().getDeliveryTag();
        String json = new String(delivery.getBody(), StandardCharsets.UTF_8);

        try {
            Figure figure = jsonb.fromJson(json, Figure.class);
            logger.info("Event alındı: {}", figure.getName());

            notificationSender.notifyStockArrival(figure);

            // Başarılı: mesaj onaylanır ve kuyruktan kalıcı olarak silinir.
            channel.basicAck(deliveryTag, false);
        } catch (JsonbException | IOException e) {
            logger.error("Mesaj işlenirken hata oluştu, DLQ'ya yönlendiriliyor: ", e);
            try {
                // requeue=false: mesajı aynı kuyruğa geri koyma (sonsuz döngü riski),
                // x-dead-letter-exchange tanımlı olduğu için otomatik olarak DLX'e gider.
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioException) {
                logger.error("Nack gönderilirken hata oluştu: ", ioException);
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            logger.info("RabbitMQ consumer bağlantıları güvenle kapatıldı.");
        } catch (IOException | TimeoutException e) {
            logger.error("Kapatma sırasında hata: ", e);
        }
    }
}
