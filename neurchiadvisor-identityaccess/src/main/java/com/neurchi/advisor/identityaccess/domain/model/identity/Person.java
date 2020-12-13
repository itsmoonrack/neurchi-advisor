package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.domain.model.ConcurrencySafeEntity;
import com.neurchi.advisor.common.domain.model.DomainEventPublisher;

import java.util.Objects;

/**
 * Contains and manages personal data about a User, including name and contact information.
 */
public class Person extends ConcurrencySafeEntity {

    private FullName name;
    private ContactInformation contactInformation;
    private TenantId tenantId;
    private User user;

    public Person(
            final TenantId tenantId,
            final FullName name,
            final ContactInformation contactInformation) {

        this.setTenantId(tenantId);
        this.setName(name);
        this.setContactInformation(contactInformation);
    }

    public void changeContactInformation(final ContactInformation contactInformation) {
        if (!this.contactInformation().equals(contactInformation)) {
            this.setContactInformation(contactInformation);

            DomainEventPublisher
                    .instance()
                    .publish(new PersonContactInformationChanged(
                            this.contactInformation(),
                            this.tenantId(),
                            this.user().username()));
        }
    }

    public void changeName(final FullName name) {
        if (!this.name().equals(name)) {
            this.setName(name);

            DomainEventPublisher
                    .instance()
                    .publish(new PersonNameChanged(
                            this.tenantId(),
                            this.user().username(),
                            this.name()));
        }
    }

    public ContactInformation contactInformation() {
        return contactInformation;
    }

    public EmailAddress emailAddress() {
        return contactInformation.emailAddress();
    }

    public FullName name() {
        return name;
    }

    public User user() {
        return user;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Person person = (Person) o;
        return this.tenantId().equals(person.tenantId()) &&
                this.user().username().equals(person.user().username());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tenantId(), this.user().username());
    }

    protected Person() {
        // Needed by Hibernate
    }

    protected void setContactInformation(final ContactInformation contactInformation) {
        this.assertArgumentNotNull(contactInformation, "The person contact information is required.");

        this.contactInformation = contactInformation;
    }

    protected void setName(final FullName name) {
        this.assertArgumentNotNull(name, "The person name is required.");

        this.name = name;
    }

    protected void setTenantId(final TenantId tenantId) {
        this.assertArgumentNotNull(tenantId, "The tenantId is required.");

        this.tenantId = tenantId;
    }

    protected TenantId tenantId() {
        return this.tenantId;
    }

    protected void setUser(final User user) {
        this.user = user;
    }
}
