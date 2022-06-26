package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.application.AccessApplicationService;
import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import com.neurchi.advisor.identityaccess.application.NotificationApplicationService;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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

    protected CacheControl cacheControlFor(final long maxAge, final TimeUnit unit) {
        return CacheControl.maxAge(maxAge, unit);
    }

    protected String userETag(final User user) {
        int hashCode = user.hashCode() + user.person().hashCode();

        try {
            StringBuilder builder = new StringBuilder(37);
            builder.append("\"0");
            DigestUtils.appendMd5DigestAsHex(Integer.toString(hashCode).getBytes(StandardCharsets.UTF_8), builder);
            builder.append('"');
            return builder.toString();
        } catch (Throwable t) {
            return Integer.toString(hashCode);
        }
    }
}
