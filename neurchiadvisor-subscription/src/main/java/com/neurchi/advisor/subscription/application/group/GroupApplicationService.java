package com.neurchi.advisor.subscription.application.group;

import com.neurchi.advisor.subscription.domain.model.collaborator.CollaboratorService;
import com.neurchi.advisor.subscription.domain.model.collaborator.Subscriber;
import com.neurchi.advisor.subscription.domain.model.group.*;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class GroupApplicationService {

    private final Logger logger = LoggerFactory.getLogger(GroupApplicationService.class);

    private final CollaboratorService collaboratorService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;
    private final GroupQueryService groupQueryService;
    private final GroupRepository groupRepository;

    GroupApplicationService(
            final SubscriptionQueryService subscriptionQueryService,
            final SubscriptionRepository subscriptionRepository,
            final GroupQueryService groupQueryService,
            final GroupRepository groupRepository,
            final CollaboratorService collaboratorService) {
        this.groupRepository = groupRepository;
        this.groupQueryService = groupQueryService;
        this.collaboratorService = collaboratorService;
        this.subscriptionQueryService = subscriptionQueryService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public void provisionGroupWithSubscription(
            final String tenantId,
            final String exclusiveOwner,
            final String subscriberId,
            final Instant createdOn,
            final String name,
            final String description,
            final String coverPhoto) {

        Tenant tenant = new Tenant(tenantId);

        String groupId =
                this.groupQueryService()
                    .groupIdOfExclusiveOwner(
                            tenantId,
                            exclusiveOwner);

        Group group = null;

        if (groupId != null) {
            group = this.groupRepository()
                    .groupOfId(
                            tenant,
                            new GroupId(groupId));
        }

        if (group == null) {
            group = this.provisionNewGroup(
                    tenant,
                    exclusiveOwner,
                    createdOn,
                    name,
                    description,
                    coverPhoto);
        }

        String subscriptionId =
                this.subscriptionQueryService()
                    .subscriptionIdOfSubscriber(
                            tenantId,
                            group.groupId().id(),
                            subscriberId);

        Subscription subscription = null;

        if (subscriptionId != null) {
            subscription = this.subscriptionRepository()
                               .subscriptionOfId(
                                        tenantId,
                                        new SubscriptionId(subscriptionId));
        }

        if (subscription == null) {
            Subscriber subscriber =
                    this.collaboratorService()
                            .subscriberFrom(
                                    group.groupId(),
                                    subscriberId);

            subscription =
                    group.startSubscription(subscriber);

            this.subscriptionRepository().save(subscription);
        }
    }

    private Group provisionNewGroup(
            final Tenant tenant,
            final String exclusiveOwner,
            final Instant createdOn,
            final String name,
            final String description,
            final String coverPhoto) {

        final Group newGroup =
                new Group(
                        tenant,
                        new GroupId(exclusiveOwner),
                        createdOn,
                        name,
                        description,
                        coverPhoto);

        this.groupRepository().save(newGroup);

        return newGroup;
    }

    private CollaboratorService collaboratorService() {
        return this.collaboratorService;
    }

    private GroupQueryService groupQueryService() {
        return this.groupQueryService;
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

    private Logger logger() {
        return this.logger;
    }
}
