package com.neurchi.advisor.advisory.domain.model.team;

import com.neurchi.advisor.advisory.domain.model.ValueObject;

import java.time.Instant;
import java.util.Objects;

final class MemberChangeTracker extends ValueObject {

    private Instant emailAddressChangedOn;
    private Instant enabledOn;
    private Instant pictureChangedOn;
    private Instant nameChangedOn;

    public boolean canChangeEmailAddress(final Instant asOfDate) {
        return this.emailAddressChangedOn().isBefore(asOfDate);
    }

    public boolean canToggleEnabling(final Instant asOfDate) {
        return this.enabledOn().isBefore(asOfDate);
    }

    public boolean canChangePicture(final Instant asOfDate) {
        return this.pictureChangedOn().isBefore(asOfDate);
    }

    public boolean canChangeName(final Instant asOfDate) {
        return this.nameChangedOn().isBefore(asOfDate);
    }

    public MemberChangeTracker emailAddressChangedOn(final Instant asOfDate) {
        return new MemberChangeTracker(this.enabledOn(), this.nameChangedOn(), this.pictureChangedOn(), asOfDate);
    }

    public MemberChangeTracker enabledOn(final Instant asOfDate) {
        return new MemberChangeTracker(asOfDate, this.nameChangedOn(), this.pictureChangedOn(), this.emailAddressChangedOn());
    }

    public MemberChangeTracker pictureChangedOn(final Instant asOfDate) {
        return new MemberChangeTracker(this.enabledOn(), this.nameChangedOn(), asOfDate, this.emailAddressChangedOn());
    }

    public MemberChangeTracker nameChangedOn(final Instant asOfDate) {
        return new MemberChangeTracker(this.enabledOn(), asOfDate, this.pictureChangedOn(), this.emailAddressChangedOn());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MemberChangeTracker that = (MemberChangeTracker) o;
        return emailAddressChangedOn.equals(that.emailAddressChangedOn) &&
                enabledOn.equals(that.enabledOn) &&
                pictureChangedOn.equals(that.pictureChangedOn) &&
                nameChangedOn.equals(that.nameChangedOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddressChangedOn, enabledOn, pictureChangedOn, nameChangedOn);
    }

    protected MemberChangeTracker(
            final Instant enabledOn,
            final Instant nameChangedOn,
            final Instant pictureChangedOn,
            final Instant emailAddressChangedOn) {

        this.setEmailAddressChangedOn(emailAddressChangedOn);
        this.setEnabledOn(enabledOn);
        this.setPictureChangedOn(pictureChangedOn);
        this.setNameChangedOn(nameChangedOn);
    }

    protected MemberChangeTracker() {
        // Needed by Hibernate.
    }

    private Instant emailAddressChangedOn() {
        return this.emailAddressChangedOn;
    }

    private Instant enabledOn() {
        return this.enabledOn;
    }

    private Instant pictureChangedOn() {
        return this.pictureChangedOn;
    }

    private Instant nameChangedOn() {
        return this.nameChangedOn;
    }

    private void setEmailAddressChangedOn(final Instant emailAddressChangedOn) {
        this.assertArgumentNotNull(emailAddressChangedOn, "Email address changed on date must be provided.");

        this.emailAddressChangedOn = emailAddressChangedOn;
    }

    private void setEnabledOn(final Instant enabledOn) {
        this.assertArgumentNotNull(enabledOn, "Enabling date must be provided.");

        this.enabledOn = enabledOn;
    }

    private void setPictureChangedOn(final Instant pictureChangedOn) {
        this.assertArgumentNotNull(pictureChangedOn, "Picture changed on date must be provided.");

        this.pictureChangedOn = pictureChangedOn;
    }

    private void setNameChangedOn(final Instant nameChangedOn) {
        this.assertArgumentNotNull(nameChangedOn, "Name changed on date must be provided.");

        this.nameChangedOn = nameChangedOn;
    }
}
