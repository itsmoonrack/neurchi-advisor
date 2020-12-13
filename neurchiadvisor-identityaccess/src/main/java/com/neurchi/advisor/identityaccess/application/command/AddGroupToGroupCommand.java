package com.neurchi.advisor.identityaccess.application.command;

public final class AddGroupToGroupCommand {

    private String tenantId;
    private String childGroupName;
    private String parentGroupName;

    public AddGroupToGroupCommand(final String tenantId, final String parentGroupName, final String childGroupName) {
        this.tenantId = tenantId;
        this.childGroupName = childGroupName;
        this.parentGroupName = parentGroupName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getChildGroupName() {
        return childGroupName;
    }

    public void setChildGroupName(final String childGroupName) {
        this.childGroupName = childGroupName;
    }

    public String getParentGroupName() {
        return parentGroupName;
    }

    public void setParentGroupName(final String parentGroupName) {
        this.parentGroupName = parentGroupName;
    }
}
