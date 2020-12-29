package com.neurchi.advisor.common.event.sourcing;

public record EventStreamId(String streamName, int streamVersion) {

    public EventStreamId(final String streamName) {
        this(streamName, 1);
    }

    public EventStreamId(final String streamNameSegment1, final String streamNameSegment2) {
        this(streamNameSegment1, streamNameSegment2, 1);
    }

    public EventStreamId(final String streamNameSegment1, final String streamNameSegment2, final int streamVersion) {
        this(streamNameSegment1 + ':' + streamNameSegment2, streamVersion);
    }

    public EventStreamId withStreamVersion(final int streamVersion) {
        return new EventStreamId(this.streamName(), streamVersion);
    }
}
