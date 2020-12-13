package com.neurchi.advisor.identityaccess.application.representation;

import com.neurchi.advisor.identityaccess.domain.model.identity.User;

public class UserRepresentation {

    private final String emailAddress;
    private final boolean enabled;
    private final String firstName;
    private final String lastName;
    private final String tenantId;
    private final String username;

    public UserRepresentation(final User user) {
        this.emailAddress = user.person().emailAddress().address();
        this.enabled = user.isEnabled();
        this.firstName = user.person().name().firstName();
        this.lastName = user.person().name().lastName();
        this.tenantId = user.tenantId().id();
        this.username = user.username();
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getUsername() {
        return this.username;
    }
}
