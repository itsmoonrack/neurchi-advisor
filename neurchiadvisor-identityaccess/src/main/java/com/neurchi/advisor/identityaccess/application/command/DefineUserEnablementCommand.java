package com.neurchi.advisor.identityaccess.application.command;

import java.time.LocalDateTime;

public final class DefineUserEnablementCommand {

    private String tenantId;
    private String username;
    private boolean enabled;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public DefineUserEnablementCommand(final String tenantId, final String username, final boolean enabled, final LocalDateTime startDate, final LocalDateTime endDate) {
        this.tenantId = tenantId;
        this.username = username;
        this.enabled = enabled;
        this.startDate = startDate;
        this.endDate = endDate;
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
}
