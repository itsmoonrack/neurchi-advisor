package com.neurchi.advisor.subscription.application.group;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public  class SubscriptionQueryService {

    public String subscriptionIdOfSubscriber(final String tenantId, final String groupId, final String subscriberId) {
        return "";
    }

    public Optional<String> subscriptionIdOfExclusiveOwner(final String tenantId, final String exclusiveOwnerId) {
        return Optional.empty();
    }
}
