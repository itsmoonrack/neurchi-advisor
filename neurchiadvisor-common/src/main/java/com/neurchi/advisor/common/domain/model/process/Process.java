package com.neurchi.advisor.common.domain.model.process;

import java.time.Duration;
import java.time.Instant;

public interface Process {

    enum ProcessCompletionType {
        NotCompleted,
        CompletedNormally,
        TimedOut
    }

    Duration allowableDuration();

    boolean canTimeout();

    Duration currentDuration();

    String description();

    boolean didProcessingComplete();

    void informTimeout(Instant timedOutDate);

    boolean isCompleted();

    boolean isTimedOut();

    boolean notCompleted();

    ProcessCompletionType processCompletionType();

    ProcessId processId();

    Instant startTime();

    TimeConstrainedProcessTracker timeConstrainedProcessTracker();

    Instant timedOutDate();

    Duration totalAllowableDuration();

    int totalRetriesPermitted();
}
