package com.neurchi.advisor.identityaccess.application.command;

public final class ActivateTenantCommand {

    private String tenantId;

    public ActivateTenantCommand(final String tenantId) {
        this.tenantId = tenantId;
    }

    public ActivateTenantCommand() {

    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }
}
