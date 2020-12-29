package com.neurchi.advisor.identityaccess.application.representation;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;

@JacksonXmlRootElement(localName = "UserInRole")
public class UserInRoleRepresentation {

    private final String emailAddress;
    private final String firstName;
    private final String lastName;
    private final String role;
    private final String tenantId;
    private final String username;

    public UserInRoleRepresentation(final User user, final String role) {
        this.emailAddress = user.person().emailAddress().address();
        this.firstName = user.person().name().firstName();
        this.lastName = user.person().name().lastName();
        this.role = role;
        this.tenantId = user.tenantId().id();
        this.username = user.username();
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getRole() {
        return this.role;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public String getUsername() {
        return this.username;
    }
}
