package com.neurchi.advisor.common.port.adapter.persistence.eventsourcing.mysql;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.event.EventStore;
import org.springframework.beans.factory.annotation.Autowired;

class MySQLJdbcEventStoreTest extends CommonTestCase {

    @Autowired
    private EventStore eventStore;

}