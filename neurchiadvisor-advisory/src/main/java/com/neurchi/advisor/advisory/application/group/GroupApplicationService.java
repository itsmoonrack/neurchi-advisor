package com.neurchi.advisor.advisory.application.group;

import com.neurchi.advisor.advisory.domain.model.group.Group;
import com.neurchi.advisor.advisory.domain.model.group.GroupId;
import com.neurchi.advisor.advisory.domain.model.group.GroupRepository;
import com.neurchi.advisor.advisory.domain.model.group.GroupSubscriptionRequestTimedOut;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionDescriptor;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwner;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.process.ProcessId;
import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTracker;
import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.neurchi.advisor.common.port.adapter.service.facebook.types.UserGroup;
import com.restfb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class GroupApplicationService {

    @Value("${security.oauth2.client.client-secret}")
    private String appSecret; // TODO: Move this

    private final GroupRepository groupRepository;
    private final GroupOwnerRepository groupOwnerRepository;
    private final TimeConstrainedProcessTrackerRepository processTrackerRepository;
    private final Logger logger = LoggerFactory.getLogger(GroupApplicationService.class);

    GroupApplicationService(
            final GroupRepository groupRepository,
            final GroupOwnerRepository groupOwnerRepository,
            final TimeConstrainedProcessTrackerRepository processTrackerRepository) {

        this.processTrackerRepository = processTrackerRepository;
        this.groupOwnerRepository = groupOwnerRepository;
        this.groupRepository = groupRepository;
    }

    @Transactional
    public void initiateSubscription(final InitiateSubscriptionCommand command) {

        try {
            final Group group =
                    this.groupRepository()
                            .groupOfId(
                                    new TenantId(command.getTenantId()),
                                    new GroupId(command.getGroupId()))
                            .orElseThrow(() -> new IllegalStateException(
                                    "Unknown group of tenant id: " +
                                    command.getTenantId() +
                                    " and group id: " +
                                    command.getGroupId()));

            group.initiateSubscription(new SubscriptionDescriptor(command.getSubscriptionId()));

            final ProcessId processId = ProcessId.existingProcessId(group.subscriptionInitiationId());

            final TimeConstrainedProcessTracker tracker =
                    this.processTrackerRepository()
                            .trackerOfProcessId(command.getTenantId(), processId)
                            .orElseThrow(() -> new IllegalStateException("Unknown tracker of process id: " + processId));

            tracker.complete();

        } catch (RuntimeException e) {
            this.logger.error("Unable to initiate subscription.", e);
            throw e;
        }
    }

    @Transactional
    public String newGroup(final NewGroupCommand command) {

        return this.newGroupWith(
                command.getTenantId(),
                command.getGroupId(),
                command.getGroupOwnerId(),
                command.getName(),
                command.getDescription(),
                command.getMemberCount(),
                command.getCreatedOn(),
                command.getCover(),
                SubscriptionAvailability.NotRequested);
    }

    @Transactional
    public String newGroupWithSubscription(final NewGroupCommand command) {

        return this.newGroupWith(
                command.getTenantId(),
                command.getGroupId(),
                command.getGroupOwnerId(),
                command.getName(),
                command.getDescription(),
                command.getMemberCount(),
                command.getCreatedOn(),
                command.getCover(),
                this.requestSubscriptionIfAvailable());
    }

    @Transactional
    public void requestGroupSubscription(final RequestGroupSubscriptionCommand command) {

        final Group group =
                this.groupRepository()
                        .groupOfId(
                                new TenantId(command.getTenantId()),
                                new GroupId(command.getGroupId()))
                        .orElseThrow(() -> new IllegalStateException(
                                "Unknown group of tenant id: " +
                                        command.getTenantId() +
                                        " and group id: " +
                                        command.getGroupId()));

        group.requestSubscription(this.requestSubscriptionIfAvailable());
    }

    @Transactional
    public void retryGroupSubscriptionRequest(final RetryGroupSubscriptionRequestCommand command) {

        final ProcessId processId = ProcessId.existingProcessId(command.getProcessId());

        final TenantId tenantId = new TenantId(command.getTenantId());

        final Group group =
                this.groupRepository()
                        .groupOfSubscriptionInitiationId(
                                tenantId,
                                processId.id())
                        .orElseThrow(() -> new IllegalStateException(
                                "Unknown group of tenant id: " +
                                        command.getTenantId() +
                                        " and subscription initiation id: " +
                                        command.getProcessId()));

        group.requestSubscription(this.requestSubscriptionIfAvailable());
    }

    @Transactional
    public void startSubscriptionInitiation(final StartSubscriptionInitiationCommand command) {

        try {
            final Group group =
                    this.groupRepository()
                            .groupOfId(
                                    new TenantId(command.getTenantId()),
                                    new GroupId(command.getGroupId()))
                            .orElseThrow(() -> new IllegalStateException(
                                    "Unknown group of tenant id: " +
                                            command.getTenantId() +
                                            " and group id: " +
                                            command.getGroupId()));

            final String timedOutEventName = GroupSubscriptionRequestTimedOut.class.getName();

            final TimeConstrainedProcessTracker tracker =
                    new TimeConstrainedProcessTracker(
                            group.tenantId().id(),
                            ProcessId.newProcessId(),
                            "Create subscription for group: " + group.name(),
                            Instant.now(),
                            Duration.ofMinutes(5),
                            3,
                            timedOutEventName);

            this.processTrackerRepository().add(tracker);

            group.startSubscriptionInitiation(tracker.processId().id());

        } catch (RuntimeException e) {
            this.logger.error("Unable to start subscription initiation.", e);
            throw e;
        }
    }

    @Transactional
    public void timeOutGroupSubscriptionRequest(final TimeOutGroupSubscriptionRequestCommand command) {

        try {
            final ProcessId processId = ProcessId.existingProcessId(command.getProcessId());

            final TenantId tenantId = new TenantId(command.getTenantId());

            final Group group =
                    this.groupRepository()
                            .groupOfSubscriptionInitiationId(
                                    tenantId,
                                    processId.id())
                            .orElseThrow(() -> new IllegalStateException(
                                    "Unknown group of tenant id: " +
                                            command.getTenantId() +
                                            " and subscription initiation id: " +
                                            command.getProcessId()));

            this.sendEmailForTimedOutProcess(group);

            group.failSubscriptionInitiation();

        } catch (RuntimeException e) {
            this.logger.error("Unable to time out group subscription request.", e);
            throw e;
        }
    }

    private void sendEmailForTimedOutProcess(final Group group) {

        // TODO: Implement

    }

    private String newGroupWith(
            final String tenantIdentity,
            final String groupIdentity,
            final String groupOwnerId,
            final String name,
            final String description,
            final Integer memberCount,
            final LocalDateTime createdOn,
            final String cover,
            final SubscriptionAvailability subscriptionAvailability) {

        final TenantId tenantId = new TenantId(tenantIdentity);
        final GroupId groupId = new GroupId(groupIdentity);

        final GroupOwner groupOwner =
                this.groupOwnerRepository()
                        .groupOwnerOfIdentity(
                                tenantId,
                                groupOwnerId)
                        .orElseThrow(() -> new IllegalStateException("Unable to find group owner: " + groupOwnerId));

        final Group group =
                new Group(
                        tenantId,
                        groupId,
                        groupOwner.groupOwnerId(),
                        name,
                        description,
                        createdOn,
                        memberCount,
                        cover,
                        subscriptionAvailability);

        this.groupRepository().add(group);

        return group.groupId().id();
    }

    private SubscriptionAvailability requestSubscriptionIfAvailable() {

        SubscriptionAvailability availability = SubscriptionAvailability.GroupAccessMemberInfoNotEnabled;

        availability = SubscriptionAvailability.Requested;

        SecurityContext context = SecurityContextHolder.getContext();

        if (context.getAuthentication() instanceof OAuth2Authentication authentication) {
            if (authentication.getDetails() instanceof OAuth2AuthenticationDetails details) {
                // TODO: optimize this
                FacebookClient facebookClient = new DefaultFacebookClient(details.getTokenValue(), this.appSecret, Version.VERSION_7_0);

                Connection<UserGroup> groupConnection = facebookClient.fetchConnection("me/groups", UserGroup.class,
                        Parameter.with("limit", 1),
                        Parameter.with("fields", "id"));

                if (groupConnection.getData().size() > 0) {
                    availability = SubscriptionAvailability.Requested;
                }
            }
        }

        return availability;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private GroupOwnerRepository groupOwnerRepository() {
        return this.groupOwnerRepository;
    }

    private TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return this.processTrackerRepository;
    }
}
