package com.neurchi.advisor.advisory.application.process;

import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTracker;
import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
public class ProcessApplicationService {

    private final TimeConstrainedProcessTrackerRepository processTrackerRepository;

    ProcessApplicationService(final TimeConstrainedProcessTrackerRepository processTrackerRepository) {
        this.processTrackerRepository = processTrackerRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 30000, initialDelay = 15000)
    public void checkForTimedOutProcesses() {
        final Stream<TimeConstrainedProcessTracker> trackers =
                this.processTrackerRepository().allTimedOut();

        trackers.forEach(TimeConstrainedProcessTracker::informProcessTimedOut);
    }

    private TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return this.processTrackerRepository;
    }
}
