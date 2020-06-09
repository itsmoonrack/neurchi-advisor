package com.neurchi.advisor.common.port.adapter.service.facebook.types;

import com.restfb.Facebook;
import com.restfb.types.AbstractFacebookType;

public class Picture extends AbstractFacebookType {

    @Facebook
    private PictureItem data;

    public Integer getHeight() {
        return data.height;
    }

    public Boolean getSilhouette() {
        return data.silhouette;
    }

    public String getUrl() {
        return data.url;
    }

    public Integer getWidth() {
        return data.width;
    }

    private static class PictureItem extends AbstractFacebookType {

        @Facebook
        private Integer height;

        @Facebook("is_silhouette")
        private Boolean silhouette;

        @Facebook
        private String url;

        @Facebook
        private Integer width;
    }
}
