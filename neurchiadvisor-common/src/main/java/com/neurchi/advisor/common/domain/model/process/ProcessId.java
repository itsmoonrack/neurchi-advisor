package com.neurchi.advisor.common.domain.model.process;

import com.neurchi.advisor.common.domain.model.AbstractId;

import java.util.UUID;

public final class ProcessId extends AbstractId {

    public static ProcessId existingProcessId(final String id) {
        return new ProcessId(id);
    }

    public static ProcessId newProcessId() {
        return new ProcessId(UUID.randomUUID().toString().toLowerCase());
    }

    protected ProcessId(final String id) {
        super(id);
    }

    protected ProcessId() {
        super();
    }
}
