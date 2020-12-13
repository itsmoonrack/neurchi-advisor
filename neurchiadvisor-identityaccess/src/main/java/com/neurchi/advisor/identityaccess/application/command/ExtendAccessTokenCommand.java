package com.neurchi.advisor.identityaccess.application.command;

import java.time.LocalDateTime;

public final class ExtendAccessTokenCommand {

    private String tenantId;
    private String username;
    private String accessToken;
    private String tokenType;
    private LocalDateTime expiresIn;

    public ExtendAccessTokenCommand(final String tenantId, final String username, final String accessToken, final String tokenType, final LocalDateTime expiresIn) {
        this.tenantId = tenantId;
        this.username = username;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(final String tokenType) {
        this.tokenType = tokenType;
    }

    public LocalDateTime getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(final LocalDateTime expiresIn) {
        this.expiresIn = expiresIn;
    }
}
