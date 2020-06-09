package com.neurchi.advisor.subscription.port.adapter.persistence.repository;

import com.neurchi.advisor.subscription.domain.model.group.Subscription;
import com.neurchi.advisor.subscription.domain.model.group.SubscriptionId;
import com.neurchi.advisor.subscription.domain.model.group.SubscriptionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class EventStoreSubscriptionRepository implements SubscriptionRepository {

    @Override
    public Subscription subscriptionOfId(final String tenantId, final SubscriptionId subscriptionId) {
        return null;
    }
}
