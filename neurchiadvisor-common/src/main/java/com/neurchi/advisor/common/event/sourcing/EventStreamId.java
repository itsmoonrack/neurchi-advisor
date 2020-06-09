package com.neurchi.advisor.common.event.sourcing;

public final class EventStreamId {

    private final String streamName;
    private final int streamVersion;

    public EventStreamId(final String streamName) {
        this(streamName, 1);
    }

    public EventStreamId(final String streamName, final int streamVersion) {
        this.streamName = streamName;
        this.streamVersion = streamVersion;
    }

    public EventStreamId(final String streamNameSegment1, final String streamNameSegment2) {
        this(streamNameSegment1, streamNameSegment2, 1);
    }

    public EventStreamId(final String streamNameSegment1, final String streamNameSegment2, final int streamVersion) {
        this(streamNameSegment1 + ':' + streamNameSegment2, streamVersion);
    }

    public String streamName() {
        return this.streamName;
    }

    public int streamVersion() {
        return this.streamVersion;
    }

    public EventStreamId withStreamVersion(final int streamVersion) {
        return new EventStreamId(this.streamName(), streamVersion);
    }
}
