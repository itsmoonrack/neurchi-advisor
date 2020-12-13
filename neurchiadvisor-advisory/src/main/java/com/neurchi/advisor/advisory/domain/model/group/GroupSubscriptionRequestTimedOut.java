package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.common.domain.model.process.ProcessId;
import com.neurchi.advisor.common.domain.model.process.ProcessTimedOut;

public class GroupSubscriptionRequestTimedOut extends ProcessTimedOut {

    GroupSubscriptionRequestTimedOut(
            final String tenantId,
            final ProcessId processId,
            final int totalRetriesPermitted,
            final int retryCount) {

        super(tenantId, processId, totalRetriesPermitted, retryCount);
    }
}
