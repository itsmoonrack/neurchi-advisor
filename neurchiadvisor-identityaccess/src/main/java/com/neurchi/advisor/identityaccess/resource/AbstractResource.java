package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.application.AccessApplicationService;
import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import com.neurchi.advisor.identityaccess.application.NotificationApplicationService;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractResource {

    @Autowired
    private AccessApplicationService accessApplicationService;
    @Autowired
    private IdentityApplicationService identityApplicationService;
    @Autowired
    private NotificationApplicationService notificationApplicationService;

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
