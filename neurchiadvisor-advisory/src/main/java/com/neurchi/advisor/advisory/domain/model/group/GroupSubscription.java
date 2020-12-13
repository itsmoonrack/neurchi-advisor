package com.neurchi.advisor.advisory.domain.model.group;

import com.neurchi.advisor.advisory.domain.model.ValueObject;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionDescriptor;

public class GroupSubscription extends ValueObject {

    private SubscriptionAvailability availability;
    private SubscriptionDescriptor descriptor;

    GroupSubscription(
            final SubscriptionDescriptor descriptor,
            final SubscriptionAvailability availability) {

        this.setAvailability(availability);
        this.setDescriptor(descriptor);
    }

    static GroupSubscription fromAvailability(final SubscriptionAvailability availability) {
        if (availability.isReady()) {
            throw new IllegalArgumentException("Cannot be created ready.");
        }

        final SubscriptionDescriptor descriptor =
                new SubscriptionDescriptor();

        return new GroupSubscription(descriptor, availability);
    }

    public SubscriptionAvailability availability() {
        return this.availability;
    }

    public SubscriptionDescriptor descriptor() {
        return this.descriptor;
    }

    public GroupSubscription nowReady(final SubscriptionDescriptor descriptor) {
        this.assertStateFalse(descriptor == null || descriptor.isUndefined(), "The subscription descriptor must be defined.");
        this.assertStateTrue(this.availability().isRequested(), "The subscription must be requested first.");

        return new GroupSubscription(descriptor, SubscriptionAvailability.Ready);
    }

    protected GroupSubscription() {
        // Needed by Hibernate.
    }

    private void setAvailability(SubscriptionAvailability availability) {
        this.assertArgumentNotNull(availability, "The availability must be provided.");

        this.availability = availability;
    }

    private void setDescriptor(SubscriptionDescriptor descriptor) {
        this.assertArgumentNotNull(descriptor, "The descriptor must be provided.");

        this.descriptor = descriptor;
    }
}
