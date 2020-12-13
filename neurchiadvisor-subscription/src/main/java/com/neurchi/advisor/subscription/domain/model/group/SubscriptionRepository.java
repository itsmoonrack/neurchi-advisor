package com.neurchi.advisor.subscription.domain.model.group;

public interface SubscriptionRepository {

    Subscription subscriptionOfId(String tenantId, SubscriptionId subscriptionId);

    void save(Subscription subscription);
}
