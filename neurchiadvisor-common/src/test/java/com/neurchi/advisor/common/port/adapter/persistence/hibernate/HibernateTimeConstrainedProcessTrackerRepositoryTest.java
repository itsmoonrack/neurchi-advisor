package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.common.domain.model.process.Process;
import com.neurchi.advisor.common.domain.model.process.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class HibernateTimeConstrainedProcessTrackerRepositoryTest extends CommonTestCase {

    private static final String TenantId = "1234567890";

    private TestableTimeConstrainedProcess process;
    private boolean received;
    @Autowired
    private TimeConstrainedProcessTrackerRepository trackerRepository;

    @Test
    public void TestCompletedProcess() throws Exception {
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<TestableTimeConstrainedProcessTimedOut>() {
            @Override
            public void handleEvent(final TestableTimeConstrainedProcessTimedOut domainEvent) {
                received = true;
                process.informTimeout(domainEvent.occurredOn());
            }

            @Override
            public Class<TestableTimeConstrainedProcessTimedOut> subscribedToType() {
                return TestableTimeConstrainedProcessTimedOut.class;
            }
        });

        process = new TestableTimeConstrainedProcess(
                TenantId,
                ProcessId.newProcessId(),
                "Testable Time Constrained Process",
                Duration.ofSeconds(5));

        TimeConstrainedProcessTracker tracker =
                process.timeConstrainedProcessTracker();

        trackerRepository.add(tracker);

        process.confirm1();

        assertFalse(received);
        assertFalse(process.isCompleted());
        assertFalse(process.didProcessingComplete());
        assertEquals(Process.ProcessCompletionType.NotCompleted, process.processCompletionType());

        process.confirm2();

        assertFalse(received);
        assertTrue(process.isCompleted());
        assertTrue(process.didProcessingComplete());
        assertEquals(Process.ProcessCompletionType.CompletedNormally, process.processCompletionType());
        assertNull(process.timedOutDate());

        tracker.informProcessTimedOut();

        assertFalse(received);
        assertFalse(process.isTimedOut());

        assertNotEquals(0, trackerRepository.allTrackers(process.tenantId()).count());
        assertEquals(0, trackerRepository.allTimedOutOf(process.tenantId()).count());
    }

    @Test
    public void TestTimedOutProcess() throws Exception {
        TestableTimeConstrainedProcess process1
                = new TestableTimeConstrainedProcess(
                        TenantId,
                        ProcessId.newProcessId(),
                        "Testable Time Constrained Process 1",
                        Duration.ofMillis(1)); // forced timeout

        TimeConstrainedProcessTracker tracker1 =
                process1.timeConstrainedProcessTracker();

        TestableTimeConstrainedProcess process2 =
                new TestableTimeConstrainedProcess(
                        TenantId,
                        ProcessId.newProcessId(),
                        "Testable Time Constrained Process 2",
                        Duration.ofSeconds(5));

        TimeConstrainedProcessTracker tracker2 =
                process2.timeConstrainedProcessTracker();

        trackerRepository.add(tracker1);
        trackerRepository.add(tracker2);

        Thread.sleep(500); // forced timeout of process1

        assertEquals(2, trackerRepository.allTrackers(process1.tenantId()).count());
        assertEquals(1, trackerRepository.allTimedOut().count());
    }
}