package com.neurchi.advisor.subscription.application.group;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public  class SubscriptionQueryService {

    public Optional<String> subscriptionIdOfExclusiveOwner(final String tenantId, final String exclusiveOwner) {
        return Optional.empty();
    }
}
