package com.neurchi.advisor.subscription;

import com.neurchi.advisor.common.event.sourcing.EventStore;
import com.neurchi.advisor.common.port.adapter.persistence.eventsourcing.mysql.MySQLJdbcEventStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class SubscriptionApplicationContext {

    public static void main(String[] args) {
        SpringApplication.run(SubscriptionApplicationContext.class, args);
    }

    @Bean
    public EventStore eventStore(final DataSource dataSource) {
        return new MySQLJdbcEventStore(dataSource);
    }
}
