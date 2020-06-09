package com.neurchi.advisor.subscription;

import com.neurchi.advisor.common.event.sourcing.EventStore;
import com.neurchi.advisor.common.port.adapter.persistence.eventsourcing.mysql.MySQLJdbcEventStore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@SpringBootApplication
public class SubscriptionApplicationContext {

    @Bean
    public EventStore eventStore(final DataSource dataSource) {
        return new MySQLJdbcEventStore(dataSource);
    }

}
