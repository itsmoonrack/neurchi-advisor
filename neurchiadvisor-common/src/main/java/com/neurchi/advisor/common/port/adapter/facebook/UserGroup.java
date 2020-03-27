package com.neurchi.advisor.common.port.adapter.facebook;

import com.restfb.Facebook;
import com.restfb.types.Group;

public class UserGroup extends Group {

    @Facebook
    private Boolean administrator;

    @Facebook
    private Long unread;

    @Facebook("member_count")
    private Long memberCount;

    public Boolean getAdministrator() {
        return administrator;
    }

    public void setAdministrator(final Boolean administrator) {
        this.administrator = administrator;
    }

    public Long getUnread() {
        return unread;
    }

    public void setUnread(final Long unread) {
        this.unread = unread;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(final Long memberCount) {
        this.memberCount = memberCount;
    }
}
