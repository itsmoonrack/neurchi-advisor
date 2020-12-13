package com.neurchi.advisor.identityaccess.domain.model.identity;


import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;

import java.util.Objects;

/**
 * A registered security principal, complete with personal name and contact information.
 */
public class User extends ConcurrencySafeEntity {

    private AccessToken accessToken;
    private Enablement enablement;
    private Person person;
    private TenantId tenantId;
    private String username;

    public void changePersonalContactInformation(final ContactInformation contactInformation) {
        this.person().changeContactInformation(contactInformation);
    }

    public void changePersonalName(final FullName personalName) {
        this.person().changeName(personalName);
    }

    public void extendAccessToken(final AccessToken accessToken) {
        if (!this.accessToken().equals(accessToken)) {
            this.assertArgumentTrue(accessToken.expiresLaterThan(this.accessToken()), "The access token must expires later than current.");

            this.setAccessToken(accessToken);

            DomainEventPublisher
                    .instance()
                    .publish(new UserAccessTokenExtended(
                            this.tenantId(),
                            this.username(),
                            accessToken));
        }
    }

    public void defineEnablement(final Enablement enablement) {
        this.setEnablement(enablement);

        DomainEventPublisher
                .instance()
                .publish(new UserEnablementChanged(
                        this.tenantId(),
                        this.username(),
                        enablement));
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public AccessToken accessToken() {
        return this.accessToken;
    }

    public Enablement enablement() {
        return enablement;
    }

    public boolean isEnabled() {
        return this.enablement().isEnablementEnabled();
    }

    public Person person() {
        return person;
    }

    public UserDescriptor userDescriptor() {
        return new UserDescriptor(
                this.tenantId(),
                this.username(),
                this.person().emailAddress().address());
    }

    public String username() {
        return username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final User user = (User) o;
        return tenantId.equals(user.tenantId) &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, username);
    }

    protected User(
            final TenantId tenantId,
            final String username,
            final AccessToken accessToken,
            final Enablement enablement,
            final Person person) {

        this.setTenantId(tenantId);
        this.setUsername(username);
        this.setAccessToken(accessToken);
        this.setEnablement(enablement);
        this.setPerson(person);

        person.setUser(this);

        DomainEventPublisher
                .instance()
                .publish(new UserRegistered(
                        this.tenantId(),
                        username,
                        person.name(),
                        person.contactInformation().emailAddress()));
    }

    protected GroupMember toGroupMember() {
        return new GroupMember(
                this.tenantId(),
                this.username(),
                GroupMemberType.User);
    }

    protected User() {
        // Needed by Hibernate
    }

    private void setUsername(final String username) {
        this.assertArgumentNotEmpty(username, "The username is required.");
        this.assertArgumentLength(username, 45, "The username must be 3 to 45 characters.");

        this.username = username;
    }

    private void setPerson(final Person person) {
        this.assertArgumentNotNull(person, "The person is required.");

        this.person = person;
    }

    private void setAccessToken(final AccessToken accessToken) {
        this.assertArgumentNotNull(accessToken, "The access token is required.");

        this.accessToken = accessToken;
    }

    private void setEnablement(final Enablement enablement) {
        this.assertArgumentNotNull(enablement, "The enablement is required.");

        this.enablement = enablement;
    }

    private void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId is required.");

        this.tenantId = tenantId;
    }
}
