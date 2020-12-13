package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.common.AssertionConcern;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
// TODO: write a component unit-test class ?
public final class AccessToken extends AssertionConcern implements Serializable {

    private String accessToken;
    private String tokenType;
    private LocalDateTime expiresIn;

    public AccessToken(final String accessToken, final String tokenType, final LocalDateTime expiresIn) {
        this.setAccessToken(accessToken);
        this.setTokenType(tokenType);
        this.setExpiresIn(expiresIn);
    }

    public String accessToken() {
        return this.accessToken;
    }

    public String tokenType() {
        return this.tokenType;
    }

    public LocalDateTime expiresIn() {
        return this.expiresIn;
    }

    public boolean expiresLaterThan(final AccessToken accessToken) {
        return this.expiresIn().isAfter(accessToken.expiresIn());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AccessToken that = (AccessToken) o;
        return accessToken.equals(that.accessToken) && tokenType.equals(that.tokenType) && expiresIn.equals(that.expiresIn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, tokenType, expiresIn);
    }

    protected AccessToken() {
        // Needed by Hibernate.
    }

    private void setAccessToken(final String accessToken) {
        this.assertArgumentNotEmpty(accessToken, "Access Token must be provided.");
        this.assertArgumentLength(accessToken, 65535, "Access Token size must be higher than 65535.");

        this.accessToken = accessToken;
    }

    private void setTokenType(final String tokenType) {
        this.assertArgumentNotEmpty(tokenType, "Token type must be provided.");
        this.assertArgumentLength(tokenType, 36, "Token type size must be higher than 65535.");

        this.tokenType = tokenType;
    }

    private void setExpiresIn(final LocalDateTime expiresIn) {
        this.assertArgumentNotNull(expiresIn, "Expires in must be provided.");
        this.assertArgumentTrue(expiresIn.isAfter(LocalDateTime.now().plusDays(60)), "Access token must be an extended token.");

        this.expiresIn = expiresIn;
    }
}
