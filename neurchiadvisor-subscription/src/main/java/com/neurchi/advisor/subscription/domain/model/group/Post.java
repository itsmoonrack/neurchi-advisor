package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.EventSourcedRootEntity;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.util.stream.Stream;

public final class Post extends EventSourcedRootEntity {

    private Tenant tenant;
    private GroupId groupId;
    private PostId postId;

    public Post(final Tenant tenant, final GroupId groupId, final PostId postId) {
    }

    public Post(final Stream<DomainEvent> eventStream, final int streamVersion) {
        super(eventStream, streamVersion);
    }
}
