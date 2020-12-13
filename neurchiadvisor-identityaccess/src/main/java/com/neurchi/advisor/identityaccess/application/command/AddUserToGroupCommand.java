package com.neurchi.advisor.identityaccess.application.command;

public final class AddUserToGroupCommand {

    private String tenantId;
    private String groupName;
    private String username;

    public AddUserToGroupCommand(final String tenantId, final String groupName, final String username) {
        this.tenantId = tenantId;
        this.groupName = groupName;
        this.username = username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
}
