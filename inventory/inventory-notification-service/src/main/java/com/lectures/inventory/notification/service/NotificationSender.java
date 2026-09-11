package com.lectures.inventory.notification.service;

import com.lectures.inventory.notification.model.Figure;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class NotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    public void notifyStockArrival(Figure figure) {
        // Gerçek senaryoda burada e-posta/push/SMS servis çağrısı yapılır.
        logger.info("Bildirim gönderildi: '{}' figürü stoklara eklendi ({} adet).",
                figure.getName(), figure.getStockQuantity());
    }
}
