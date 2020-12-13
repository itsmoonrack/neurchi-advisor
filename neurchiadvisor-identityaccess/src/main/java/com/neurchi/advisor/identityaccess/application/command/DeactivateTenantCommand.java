package com.neurchi.advisor.identityaccess.application.command;

public final class DeactivateTenantCommand {

    private String tenantId;

    public DeactivateTenantCommand(final String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }
}
