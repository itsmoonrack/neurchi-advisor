package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.application.AccessApplicationService;
import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import com.neurchi.advisor.identityaccess.application.NotificationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;

import java.time.Duration;

public class AbstractResource {

    @Autowired
    private AccessApplicationService accessApplicationService;
    @Autowired
    private IdentityApplicationService identityApplicationService;
    @Autowired
    private NotificationApplicationService notificationApplicationService;

    protected CacheControl cacheControlFor(final Duration duration) {
        return CacheControl.maxAge(duration);
    }

    protected AccessApplicationService accessApplicationService() {
        return this.accessApplicationService;
    }

    protected IdentityApplicationService identityApplicationService() {
        return this.identityApplicationService;
    }

    protected NotificationApplicationService notificationApplicationService() {
        return this.notificationApplicationService;
    }
}
