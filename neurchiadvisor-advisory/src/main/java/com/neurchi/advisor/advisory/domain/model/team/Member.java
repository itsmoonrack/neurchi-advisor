package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;

import java.time.Instant;
import java.util.Objects;

public abstract class Member extends ConcurrencySafeEntity {

    private MemberChangeTracker changeTracker;
    private String emailAddress;
    private boolean enabled = true;
    private String firstName;
    private String lastName;
    private String name;
    private String picture;
    private TenantId tenantId;
    private String username;

    public Member(
            final TenantId tenantId,
            final String username,
            final String name,
            final String firstName,
            final String lastName,
            final String picture,
            final String emailAddress,
            final Instant initializedOn) {

        this(tenantId, username, name, firstName, lastName, picture, emailAddress);

        this.setChangeTracker(
                new MemberChangeTracker(
                        initializedOn,
                        initializedOn,
                        initializedOn,
                        initializedOn));
    }

    public void changePicture(final String picture, final Instant asOfDate) {
        if (this.changeTracker().canChangePicture(asOfDate)) {
            this.setPicture(picture);
            this.setChangeTracker(this.changeTracker().pictureChangedOn(asOfDate));
        }
    }

    public void changeEmailAddress(final String emailAddress, final Instant asOfDate) {
        if (this.changeTracker().canChangeEmailAddress(asOfDate)) {
            this.setEmailAddress(emailAddress);
            this.setChangeTracker(this.changeTracker().emailAddressChangedOn(asOfDate));
        }
    }

    public void changeName(final String name, final String firstName, final String lastName, final Instant asOfDate) {
        if (this.changeTracker().canChangeName(asOfDate)) {
            this.setName(name);
            this.setFirstName(firstName);
            this.setLastName(lastName);
            this.setChangeTracker(this.changeTracker().nameChangedOn(asOfDate));
        }
    }

    public void disable(final Instant asOfDate) {
        if (this.changeTracker().canToggleEnabling(asOfDate)) {
            this.setEnabled(false);
            this.setChangeTracker(this.changeTracker().enabledOn(asOfDate));
        }
    }

    public void enable(final Instant asOfDate) {
        if (this.changeTracker().canToggleEnabling(asOfDate)) {
            this.setEnabled(true);
            this.setChangeTracker(this.changeTracker().enabledOn(asOfDate));
        }
    }

    public String emailAddress() {
        return this.emailAddress;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String firstName() {
        return this.firstName;
    }

    public String lastName() {
        return this.lastName;
    }

    public String picture() {
        return this.picture;
    }

    public String name() {
        return this.name;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public String username() {
        return this.username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Member member = (Member) o;
        return tenantId.equals(member.tenantId) &&
                username.equals(member.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, username);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "tenantId=" + tenantId +
                ", username='" + username + '\'' +
                ", enabled=" + enabled +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", name='" + name + '\'' +
                ", picture='" + picture + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }

    protected Member(
            final TenantId tenantId,
            final String username,
            final String name,
            final String firstName,
            final String lastName,
            final String picture,
            final String emailAddress) {

        this.setEmailAddress(emailAddress);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPicture(picture);
        this.setTenantId(tenantId);
        this.setName(name);
        this.setUsername(username);
    }

    protected Member() {
        // Needed by Hibernate.
    }

    private MemberChangeTracker changeTracker() {
        return this.changeTracker;
    }

    private void setChangeTracker(final MemberChangeTracker changeTracker) {
        this.changeTracker = changeTracker;
    }

    private void setEmailAddress(final String emailAddress) {
        if (emailAddress != null) {
            this.assertArgumentLength(emailAddress, 100, "Email address must be 100 characters or less.");
        }

        this.emailAddress = emailAddress;
    }

    private void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private void setFirstName(final String firstName) {
        if (firstName != null) {
            this.assertArgumentLength(firstName, 50, "First name must be 50 characters or less.");
        }

        this.firstName = firstName;
    }

    private void setPicture(final String picture) {
        if (picture != null) {
            this.assertArgumentLength(picture, 255, "Picture must be 255 characters or less.");
        }

        this.picture = picture;
    }

    private void setLastName(final String lastName) {
        if (lastName != null) {
            this.assertArgumentLength(lastName, 50, "Last name must be 50 characters or less.");
        }

        this.lastName = lastName;
    }

    private void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenant id must be provided.");

        this.tenantId = tenantId;
    }

    private void setUsername(final String username) {
        this.assertArgumentNotEmpty(username, "The username must be provided.");
        this.assertArgumentLength(username, 250, "The username must be 250 characters or less.");

        this.username = username;
    }

    private void setName(final String name) {
        this.assertArgumentNotEmpty(name, "The name must be provided.");
        this.assertArgumentLength(name, 250, "The name must be 250 characters or less.");

        this.name = name;
    }
}
