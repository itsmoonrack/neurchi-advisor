package com.neurchi.advisor.identityaccess.application.command;

import java.time.LocalDateTime;

public final class RegisterUserCommand {

    private String tenantId;
    private String invitationIdentifier;
    private String username;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String emailAddress;

    public RegisterUserCommand(
            final String tenantId,
            final String invitationIdentifier,
            final String username,
            final String firstName,
            final String lastName,
            final boolean enabled,
            final LocalDateTime startDate,
            final LocalDateTime endDate,
            final String emailAddress) {

        this.tenantId = tenantId;
        this.invitationIdentifier = invitationIdentifier;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = enabled;
        this.startDate = startDate;
        this.endDate = endDate;
        this.emailAddress = emailAddress;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getInvitationIdentifier() {
        return invitationIdentifier;
    }

    public void setInvitationIdentifier(final String invitationIdentifier) {
        this.invitationIdentifier = invitationIdentifier;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(final LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(final LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
