package com.neurchi.advisor.identityaccess.application.command;

public final class ChangeUserPersonalNameCommand {

    private String tenantId;
    private String username;
    private String firstName;
    private String lastName;

    public ChangeUserPersonalNameCommand(final String tenantId, final String username, final String firstName, final String lastName) {
        this.tenantId = tenantId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
