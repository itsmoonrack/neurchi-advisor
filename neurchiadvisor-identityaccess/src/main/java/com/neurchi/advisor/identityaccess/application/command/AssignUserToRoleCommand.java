package com.neurchi.advisor.identityaccess.application.command;

public final class AssignUserToRoleCommand {

    private String tenantId;
    private String username;
    private String roleName;

    public AssignUserToRoleCommand(final String tenantId, final String username, final String roleName) {
        this.tenantId = tenantId;
        this.username = username;
        this.roleName = roleName;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }
}
