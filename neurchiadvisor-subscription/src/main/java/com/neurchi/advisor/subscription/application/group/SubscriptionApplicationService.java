package com.neurchi.advisor.subscription.application.group;

import com.neurchi.advisor.subscription.domain.model.collaborator.CollaboratorService;
import com.neurchi.advisor.subscription.domain.model.group.GroupRepository;
import com.neurchi.advisor.subscription.domain.model.group.Subscription;
import com.neurchi.advisor.subscription.domain.model.group.SubscriptionId;
import com.neurchi.advisor.subscription.domain.model.group.SubscriptionRepository;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubscriptionApplicationService {

    private final Logger logger = LoggerFactory.getLogger(SubscriptionApplicationService.class);

    private final GroupRepository groupRepository;
    private final CollaboratorService collaboratorService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;

    SubscriptionApplicationService(
            final SubscriptionQueryService subscriptionQueryService,
            final SubscriptionRepository subscriptionRepository,
            final GroupRepository groupRepository,
            final CollaboratorService collaboratorService) {
        this.groupRepository = groupRepository;
        this.collaboratorService = collaboratorService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void startExclusiveSubscriptionWithGroup(
            final String tenantId,
            final String exclusiveOwner,
            final String creatorId,
            final String administratorId) {

        final Tenant tenant = new Tenant(tenantId);

        final Subscription subscription =
                this.subscriptionQueryService()
                        .subscriptionIdOfExclusiveOwner(
                                tenantId,
                                exclusiveOwner)
                        .map(subscriptionId ->
                                this.subscriptionRepository()
                                        .subscriptionOfId(
                                                tenantId,
                                                new SubscriptionId(subscriptionId)))
                        .orElseGet(() -> this.startNewSubscription(tenant, creatorId, administratorId, exclusiveOwner));


    }

    private Subscription startNewSubscription(
            final Tenant tenant,
            final String creatorId,
            final String administratorId,
            final String exclusiveOwner) {

        return null;
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private SubscriptionQueryService subscriptionQueryService() {
        return this.subscriptionQueryService;
    }

    private SubscriptionRepository subscriptionRepository() {
        return this.subscriptionRepository;
    }
}
