package com.neurchi.advisor.common.domain.model.process;

import com.neurchi.advisor.common.CommonTestCase;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.domain.model.DomainEventSubscriber;
import com.neurchi.advisor.common.domain.model.process.Process.ProcessCompletionType;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class TimeConstrainedProcessTest extends CommonTestCase {

    private static final String TenantId = "1234567890";

    private TestableTimeConstrainedProcess process;
    private boolean received;
    private int retryCount;

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

        process.confirm1();

        assertFalse(received);
        assertFalse(process.isCompleted());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.NotCompleted, process.processCompletionType());

        process.confirm2();

        assertFalse(received);
        assertTrue(process.isCompleted());
        assertTrue(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.CompletedNormally, process.processCompletionType());
        assertNull(process.timedOutDate());

        tracker.informProcessTimedOut();

        assertFalse(received);
        assertFalse(process.isTimedOut());
    }

    @Test
    public void TestTimedOutProcess() throws Exception {
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
                Duration.ofMillis(50));

        TimeConstrainedProcessTracker tracker =
                process.timeConstrainedProcessTracker();

        Thread.sleep(100); // forced timeout of process

        tracker.informProcessTimedOut();

        assertTrue(received);
        assertTrue(process.isCompleted());
        assertTrue(process.isTimedOut());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.TimedOut, process.processCompletionType());
        assertNotNull(process.timedOutDate());
    }

    @Test
    public void TestTimedOutProcessWithRetries() throws Exception {
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<TestableTimeConstrainedProcessTimedOut>() {
            @Override
            public void handleEvent(final TestableTimeConstrainedProcessTimedOut domainEvent) {
                received = true;
                retryCount = domainEvent.retryCount();

                if (domainEvent.retryCount() == domainEvent.totalRetriesPermitted()) {
                    process.informTimeout(domainEvent.occurredOn());
                }
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
                Duration.ofMillis(50),
                3);

        TimeConstrainedProcessTracker tracker =
                process.timeConstrainedProcessTracker();

        Thread.sleep(50); // forced timeout of process

        tracker.informProcessTimedOut();

        assertTrue(received);
        assertEquals(1, retryCount);
        assertFalse(process.isCompleted());
        assertFalse(process.isTimedOut());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.NotCompleted, process.processCompletionType());
        assertNull(process.timedOutDate());

        Thread.sleep(50); // forced timeout of process

        tracker.informProcessTimedOut();

        assertTrue(received);
        assertEquals(2, retryCount);
        assertFalse(process.isCompleted());
        assertFalse(process.isTimedOut());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.NotCompleted, process.processCompletionType());
        assertNull(process.timedOutDate());

        Thread.sleep(50); // forced timeout of process

        tracker.informProcessTimedOut();

        assertTrue(received);
        assertEquals(3, retryCount);
        assertTrue(process.isCompleted());
        assertTrue(process.isTimedOut());
        assertFalse(process.didProcessingComplete());
        assertEquals(ProcessCompletionType.TimedOut, process.processCompletionType());
        assertNotNull(process.timedOutDate());
    }

}