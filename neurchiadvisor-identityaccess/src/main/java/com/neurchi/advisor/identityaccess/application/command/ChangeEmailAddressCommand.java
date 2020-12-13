package com.neurchi.advisor.identityaccess.application.command;

public final class ChangeEmailAddressCommand {

    private String tenantId;
    private String username;
    private String emailAddress;

    public ChangeEmailAddressCommand(final String tenantId, final String username, final String emailAddress) {
        this.tenantId = tenantId;
        this.username = username;
        this.emailAddress = emailAddress;
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

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
