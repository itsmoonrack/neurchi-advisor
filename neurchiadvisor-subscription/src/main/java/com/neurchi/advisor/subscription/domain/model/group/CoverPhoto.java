package com.neurchi.advisor.subscription.domain.model.group;

import java.util.Objects;

public final class CoverPhoto {

    private String coverId;
    private Integer offsetX;
    private Integer offsetY;
    private String source;

    public CoverPhoto(final String coverId, final Integer offsetX, final Integer offsetY, final String source) {
        this.coverId = coverId;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.source = source;
    }

    public String coverId() {
        return coverId;
    }

    public Integer offsetX() {
        return offsetX;
    }

    public Integer offsetY() {
        return offsetY;
    }

    public String source() {
        return source;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CoverPhoto that = (CoverPhoto) o;
        return coverId.equals(that.coverId) &&
                offsetX.equals(that.offsetX) &&
                offsetY.equals(that.offsetY) &&
                source.equals(that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverId, offsetX, offsetY, source);
    }

    @Override
    public String toString() {
        return "CoverPhoto{" +
                "coverId=" + coverId +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", source='" + source + '\'' +
                '}';
    }
}
