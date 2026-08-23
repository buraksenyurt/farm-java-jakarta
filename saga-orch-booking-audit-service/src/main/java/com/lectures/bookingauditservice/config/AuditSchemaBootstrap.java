package com.lectures.bookingauditservice.config;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

@ApplicationScoped
public class AuditSchemaBootstrap {

    @Resource(lookup = "java:app/jdbc/auditdb")
    private DataSource dataSource;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object init) {
        String ddl = """
            CREATE TABLE IF NOT EXISTS booking_attempts (
                id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                event_id       BIGINT NOT NULL,
                customer_id    BIGINT NOT NULL,
                seat_count     INT NOT NULL,
                status         VARCHAR(20) NOT NULL,
                failure_reason VARCHAR(500),
                attempt_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        } catch (SQLException e) {
            throw new RuntimeException("Audit tablosu oluşturulamadı", e);
        }
    }
}
