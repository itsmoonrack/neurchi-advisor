package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.util.Objects;
import java.util.regex.Pattern;

public final class EmailAddress extends AssertionConcern {

    private String address;

    public EmailAddress(final String address) {
        this.setAddress(address);
    }

    public String address() {
        return address;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final EmailAddress that = (EmailAddress) o;
        return address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    protected EmailAddress() {
        // Needed by Hibernate
    }

    private void setAddress(final String address) {
        this.assertArgumentNotNull(address, "Email address must be provided.");
        this.assertArgumentLength(address, 1, 100, "The email address must 100 characters or less.");
        this.assertArgumentTrue(
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", address),
                "Email address format is invalid.");

        this.address = address;
    }

}
