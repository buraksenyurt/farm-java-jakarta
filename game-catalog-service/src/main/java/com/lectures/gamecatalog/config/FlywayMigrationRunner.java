package com.lectures.gamecatalog.config;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class FlywayMigrationRunner {

    private static final Logger logger = LoggerFactory.getLogger(FlywayMigrationRunner.class);

    @Resource(lookup = "java:app/jdbc/gamecatalog")
    private DataSource dataSource;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        logger.info("Flyway migration işlemi başlıyor...");

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
        flyway.migrate();

        logger.info("Flyway migration işlemi tamamlandı...");
    }
}
