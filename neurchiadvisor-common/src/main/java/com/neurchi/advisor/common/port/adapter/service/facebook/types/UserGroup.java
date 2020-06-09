package com.neurchi.advisor.common.port.adapter.service.facebook.types;

import com.restfb.Facebook;
import com.restfb.types.Group;

import java.util.Date;

public class UserGroup extends Group {

    @Facebook
    private Boolean administrator;

    @Facebook
    private Long unread;

    @Facebook("member_count")
    private Integer memberCount;

    @Facebook("created_time")
    private Date createdTime;

    @Facebook
    private Picture picture;

    public Boolean isAdministrator() {
        return administrator;
    }

    public Long getUnread() {
        return unread;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Picture getPicture() {
        return picture;
    }

}
