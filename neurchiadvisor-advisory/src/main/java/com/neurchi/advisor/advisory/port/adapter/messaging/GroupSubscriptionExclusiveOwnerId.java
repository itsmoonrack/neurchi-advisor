package com.neurchi.advisor.advisory.port.adapter.messaging;

import java.util.Optional;

public final class GroupSubscriptionExclusiveOwnerId {

    private static final String PREFIX = "advisor.group.subscription.";

    private String id;

    public static Optional<GroupSubscriptionExclusiveOwnerId> fromEncodedId(final String encodedId) {

        if (GroupSubscriptionExclusiveOwnerId.isValid(encodedId)) {
            return Optional.of(new GroupSubscriptionExclusiveOwnerId(encodedId.substring(PREFIX.length())));
        }

        return Optional.empty();
    }

    public static boolean isValid(final String encodedId) {
        return encodedId.startsWith(PREFIX) && encodedId.length() > PREFIX.length();
    }

    public GroupSubscriptionExclusiveOwnerId(final String id) {
        this.id = id;
    }

    public String encoded() {
        return PREFIX + this.id;
    }

    public String id() {
        return this.id;
    }
}
