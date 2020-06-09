package com.neurchi.advisor.subscription.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.EventSourcedRootEntity;
import com.neurchi.advisor.subscription.domain.model.collaborator.Administrator;
import com.neurchi.advisor.subscription.domain.model.collaborator.Participant;
import com.neurchi.advisor.subscription.domain.model.tenant.Tenant;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public final class Group extends EventSourcedRootEntity {

    private Tenant tenant;
    private GroupId groupId;
    private Instant createdOn;
    private String description;
    private String name;
    private Participant creator;
    private Set<Administrator> administratedBy;
    private CoverPhoto cover;
    private Integer memberCount;

    public Group(
            final Tenant tenant,
            final GroupId groupId,
            final Instant createdOn,
            final Participant creator,
            final String name,
            final String description,
            final CoverPhoto cover) {

        this.assertArgumentNotNull(creator, "The creator must be provided.");
        this.assertArgumentNotNull(groupId, "The group id must be provided.");
        this.assertArgumentNotNull(createdOn, "The created on date must be provided.");
        this.assertArgumentNotEmpty(name, "The name must be provided.");
        this.assertArgumentNotNull(tenant, "The tenant must be provided.");

        this.apply(new SubscriptionStarted(creator, description, groupId, createdOn, cover, name, tenant));
    }

    public Group(final Stream<DomainEvent> eventStream, final int streamVersion) {
        super(eventStream, streamVersion);
    }

    public void amendMemberCount(final Participant participant, final int memberCount) {
        this.assertArgumentNotNull(participant, "The participant must be provided.");

        if (this.memberCount() != memberCount) {
            this.apply(new GroupMemberCountChanged(this.tenant(), this.groupId(), participant, memberCount, false));
        }
    }

    public void amendMemberCount(final Administrator administrator, final int memberCount) {
        this.assertArgumentNotNull(administrator, "The administrator must be provided.");

        if (this.memberCount() != memberCount) {
            this.apply(new GroupMemberCountChanged(this.tenant(), this.groupId(), administrator, memberCount, true));
        }
    }

    public int memberCount() {
        return this.memberCount;
    }

    public void assignAdministrator(final Administrator administrator) {
        this.assertArgumentNotNull(administrator, "The administrator must be provided.");

        if (!this.administratedBy().contains(administrator)) {
            this.apply(new GroupAdministered(this.tenant(), this.groupId(), administrator));
        }
    }

    public void withholdAdministrator(final Administrator administrator) {
        this.assertArgumentNotNull(administrator, "The administrator must be provided.");

        if (this.administratedBy().contains(administrator)) {
            this.apply(new GroupAdminWithhold(this.tenant(), this.groupId(), administrator));
        }
    }

    public GroupId groupId() {
        return groupId;
    }

    public void changeDescription(final String description) {
        if (!Objects.equals(description, this.description())) {
            this.apply(new GroupDescriptionChanged(this.tenant(), this.groupId(), description));
        }
    }

    public String description() {
        return description;
    }

    public void rename(final String name) {
        this.assertArgumentNotEmpty(name, "The name must be provided.");

        if (!this.name().equals(name)) {
            this.apply(new GroupRenamed(this.tenant(), this.groupId(), name));
        }
    }

    public String name() {
        return name;
    }

    public Participant creator() {
        return creator;
    }

    public Set<Administrator> allAdministratedBy() {
        return Collections.unmodifiableSet(this.administratedBy());
    }

    public void changeCoverPhoto(final CoverPhoto cover) {
        if (!Objects.equals(this.cover(), cover)) {
            this.apply(new GroupCoverChanged(this.tenant(), this.groupId(), cover));
        }
    }

    public CoverPhoto cover() {
        return cover;
    }

    public Tenant tenant() {
        return tenant;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Group group = (Group) o;
        return tenant.equals(group.tenant) &&
                groupId.equals(group.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, groupId);
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId=" + groupId +
                ", name='" + name + "'" +
                ", tenant=" + tenant +
                '}';
    }

    protected void when(final SubscriptionStarted event) {
        this.tenant = event.tenant();
        this.groupId = event.groupId();
        this.description = event.description();
        this.name = event.name();
        this.creator = event.creator();
        this.cover = event.cover();
        this.createdOn = event.occurredOn();
        this.administratedBy = new HashSet<>();
    }

    protected void when(final GroupDescriptionChanged event) {
        this.description = event.description();
    }

    protected void when(final GroupRenamed event) {
        this.name = event.name();
    }

    protected void when(final GroupAdministered event) {
        this.administratedBy().add(event.administrator());
    }

    protected void when(final GroupAdminWithhold event) {
        this.administratedBy().remove(event.administrator());
    }

    protected void when(final GroupCoverChanged event) {
        this.cover = event.cover();
    }

    protected void when(final GroupMemberCountChanged event) {
        this.memberCount = event.memberCount();
    }

    private Set<Administrator> administratedBy() {
        return this.administratedBy;
    }

}
