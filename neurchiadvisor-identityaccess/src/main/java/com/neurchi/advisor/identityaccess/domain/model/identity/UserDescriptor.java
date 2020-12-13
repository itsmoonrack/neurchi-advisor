package com.neurchi.advisor.identityaccess.domain.model.identity;

import java.util.Objects;

public final class UserDescriptor {

    private final String emailAddress;
    private final TenantId tenantId;
    private final String username;

    public static UserDescriptor nullDescriptorInstance() {
        return new UserDescriptor();
    }

    public String emailAddress() {
        return emailAddress;
    }

    public boolean isNullDescriptor() {
        return emailAddress == null || tenantId == null || username == null;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String username() {
        return username;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final UserDescriptor that = (UserDescriptor) o;

        return Objects.equals(tenantId, that.tenantId)
                && Objects.equals(username, that.username)
                && Objects.equals(emailAddress, that.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, username, emailAddress);
    }

    UserDescriptor(final TenantId tenantId, final String username, final String emailAddress) {
        this.emailAddress = emailAddress;
        this.tenantId = tenantId;
        this.username = username;
    }

    private UserDescriptor() {
        this(null, null, null);
    }
}
