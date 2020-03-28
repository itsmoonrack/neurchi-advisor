package com.neurchi.advisor.group.domain.model.group;

import com.neurchi.advisor.common.domain.model.DomainEvent;
import com.neurchi.advisor.common.domain.model.EventSourcedRootEntity;
import com.neurchi.advisor.group.domain.model.collaborator.Administrator;
import com.neurchi.advisor.group.domain.model.collaborator.Participant;
import com.neurchi.advisor.group.domain.model.tenant.Tenant;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class Group extends EventSourcedRootEntity {

    private GroupId groupId;
    private String description;
    private String name;
    private Participant creator;
    private Set<Administrator> administratedBy;
    private CoverPhoto cover;
    private Tenant tenant;

    public Group(
            final Tenant tenant,
            final GroupId groupId,
            final Participant creator,
            final String name,
            final String description,
            final CoverPhoto cover) {

        this.assertArgumentNotNull(creator, "The creator must be provided.");
        this.assertArgumentNotEmpty(description, "The description must be provided.");
        this.assertArgumentNotNull(groupId, "The group id must be provided.");
        this.assertArgumentNotEmpty(name, "The name must be provided.");
        this.assertArgumentNotNull(tenant, "The tenant must be provided.");

        this.apply(new GroupSubscribed(creator, description, groupId, cover, name, tenant));
    }

    public Group(final Stream<DomainEvent> eventStream, final int streamVersion) {
        super(eventStream, streamVersion);
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
            this.apply(new GroupAdministratorWithhold(this.tenant(), this.groupId(), administrator));
        }
    }

    public GroupId groupId() {
        return groupId;
    }

    public void changeDescription(final String description) {
        this.assertArgumentNotEmpty(description, "The description must be provided.");

        this.apply(new GroupDescriptionChanged(this.tenant(), this.groupId(), description));
    }

    public String description() {
        return description;
    }

    public void rename(final String name) {
        this.assertArgumentNotEmpty(name, "The name must be provided.");

        this.apply(new GroupRenamed(this.tenant(), this.groupId(), name));
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

    public CoverPhoto cover() {
        return cover;
    }

    public Tenant tenant() {
        return tenant;
    }

    protected void when(final GroupSubscribed event) {
        this.tenant = event.tenant();
        this.groupId = event.groupId();
        this.description = event.description();
        this.name = event.name();
        this.creator = event.creator();
        this.cover = event.cover();
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

    protected void when(final GroupAdministratorWithhold event) {
        this.administratedBy().remove(event.administrator());
    }

    private Set<Administrator> administratedBy() {
        return this.administratedBy;
    }

}
