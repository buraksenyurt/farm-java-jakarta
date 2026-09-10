package com.lectures.inventory.events.repository;

import com.lectures.inventory.events.model.Figure;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FigureRepository {

    private static final Logger logger = LoggerFactory.getLogger(FigureRepository.class);

    public void save(Figure figure) {
        // Gerçek senaryoda burada Jakarta Persistence (EntityManager) kullanılabilir.
        logger.info("Mock DB: {} adlı figürden {} adet kaydedildi.",
                figure.getName(), figure.getStockQuantity());
    }
}
