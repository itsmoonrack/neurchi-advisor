package com.neurchi.advisor.identityaccess.application.command;

import java.time.LocalDateTime;

public final class ProvisionTenantCommand {

    private String tenantName;
    private String tenantDescription;
    private String administratorUsername;
    private String administratorFirstName;
    private String administratorLastName;
    private String administratorAccessToken;
    private String administratorTokenType;
    private LocalDateTime administratorExpiresIn;
    private String emailAddress;

    public ProvisionTenantCommand(
            final String tenantName,
            final String tenantDescription,
            final String administratorUsername,
            final String administratorFirstName,
            final String administratorLastName,
            final String administratorAccessToken,
            final String administratorTokenType,
            final LocalDateTime administratorExpiresIn,
            final String emailAddress) {

        this.tenantName = tenantName;
        this.tenantDescription = tenantDescription;
        this.administratorUsername = administratorUsername;
        this.administratorFirstName = administratorFirstName;
        this.administratorLastName = administratorLastName;
        this.administratorAccessToken = administratorAccessToken;
        this.administratorTokenType = administratorTokenType;
        this.administratorExpiresIn = administratorExpiresIn;
        this.emailAddress = emailAddress;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(final String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantDescription() {
        return tenantDescription;
    }

    public void setTenantDescription(final String tenantDescription) {
        this.tenantDescription = tenantDescription;
    }

    public String getAdministratorUsername() {
        return administratorUsername;
    }

    public void setAdministratorUsername(final String administratorUsername) {
        this.administratorUsername = administratorUsername;
    }

    public String getAdministratorFirstName() {
        return administratorFirstName;
    }

    public void setAdministratorFirstName(final String administratorFirstName) {
        this.administratorFirstName = administratorFirstName;
    }

    public String getAdministratorLastName() {
        return administratorLastName;
    }

    public void setAdministratorLastName(final String administratorLastName) {
        this.administratorLastName = administratorLastName;
    }

    public String getAdministratorAccessToken() {
        return administratorAccessToken;
    }

    public void setAdministratorAccessToken(final String administratorAccessToken) {
        this.administratorAccessToken = administratorAccessToken;
    }

    public String getAdministratorTokenType() {
        return administratorTokenType;
    }

    public void setAdministratorTokenType(final String administratorTokenType) {
        this.administratorTokenType = administratorTokenType;
    }

    public LocalDateTime getAdministratorExpiresIn() {
        return administratorExpiresIn;
    }

    public void setAdministratorExpiresIn(final LocalDateTime administratorExpiresIn) {
        this.administratorExpiresIn = administratorExpiresIn;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
