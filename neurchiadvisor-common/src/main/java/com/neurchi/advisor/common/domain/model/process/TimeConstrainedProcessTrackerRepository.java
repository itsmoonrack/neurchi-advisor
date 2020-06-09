package com.neurchi.advisor.common.domain.model.process;

import java.util.Optional;
import java.util.stream.Stream;

public interface TimeConstrainedProcessTrackerRepository {

    void add(TimeConstrainedProcessTracker timeConstrainedProcessTracker);

    Stream<TimeConstrainedProcessTracker> allTimedOut();

    Stream<TimeConstrainedProcessTracker> allTimedOutOf(String tenantId);

    Stream<TimeConstrainedProcessTracker> allTrackers(String tenantId);

    void save(TimeConstrainedProcessTracker timeConstrainedProcessTracker);

    Optional<TimeConstrainedProcessTracker> trackerOfProcessId(String tenantId, ProcessId processId);
}
