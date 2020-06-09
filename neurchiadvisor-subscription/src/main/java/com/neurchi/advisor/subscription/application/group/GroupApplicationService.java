package com.neurchi.advisor.subscription.application.group;

import com.neurchi.advisor.subscription.application.command.SubscribeToGroupCommand;
import com.neurchi.advisor.subscription.domain.model.collaborator.Administrator;
import com.neurchi.advisor.subscription.domain.model.collaborator.CollaboratorService;
import com.neurchi.advisor.subscription.domain.model.collaborator.Participant;
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

    private final GroupRepository groupRepository;
    private final CollaboratorService collaboratorService;
    private final SubscriptionQueryService subscriptionQueryService;
    private final SubscriptionRepository subscriptionRepository;

    GroupApplicationService(
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

    public void subscribeToGroup(final SubscribeToGroupCommand command) {

        try {
            final Tenant tenant = new Tenant(command.getTenantId());

            final GroupId groupId = new GroupId(command.getGroupId());

            final CoverPhoto coverPhoto;

            if (command.getCoverId() == null) {
                coverPhoto = null;
            } else {
                coverPhoto =
                        new CoverPhoto(
                                command.getCoverId(),
                                command.getOffsetX(),
                                command.getOffsetY(),
                                command.getCoverSource());
            }

            final Group group =
                    this.groupRepository()
                            .groupOfId(
                                    tenant,
                                    groupId)
                            .orElseGet(() -> this.subscribeToNewGroup(
                                    tenant,
                                    groupId,
                                    command.getCreatedOn(),
                                    command.getIdentity(),
                                    command.getName(),
                                    command.getDescription(),
                                    coverPhoto));

            group.rename(command.getName());
            group.changeDescription(command.getDescription());
            group.changeCoverPhoto(coverPhoto);

            if (command.isAdministrator()) {
                final Administrator administrator =
                        this.collaboratorService()
                                .administratorFrom(groupId, command.getIdentity());

                group.assignAdministrator(administrator);
                group.amendMemberCount(administrator, command.getMemberCount());
            }

            this.groupRepository().save(group);

            this.logger().info("Successfully subscribed to {}", group.name());

        } catch (RuntimeException e) {
            this.logger().error("Failed to subscribe to " + command.getName() + " [" + command.getGroupId() + "]", e);
        }

    }

    private Group subscribeToNewGroup(
            final Tenant tenant,
            final GroupId groupId,
            final Instant createdOn,
            final String participantId,
            final String name,
            final String description,
            final CoverPhoto coverPhoto) {

        final Participant participant =
                this.collaboratorService()
                        .participantFrom(groupId, participantId);

        return new Group(
                tenant,
                groupId,
                createdOn,
                participant,
                name,
                description,
                coverPhoto);
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

    private Logger logger() {
        return this.logger;
    }
}
