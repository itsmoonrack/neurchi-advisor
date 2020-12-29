package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.application.representation.UserInRoleRepresentation;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_JSON;
import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_XML;
import static java.util.concurrent.TimeUnit.SECONDS;

@Controller
@RequestMapping(value = "/tenants/{tenantId}/users", produces = {ID_ADVISOR_TYPE_JSON, ID_ADVISOR_TYPE_XML})
public class UserResource extends AbstractResource {

    @GetMapping(path = "{username}/inRole/{roleName}")
    public ResponseEntity<UserInRoleRepresentation> getUserInRole(
            @PathVariable final String tenantId,
            @PathVariable final String username,
            @PathVariable final String roleName) {

        try {
            return this.accessApplicationService()
                    .userInRole(tenantId, username, roleName)
                    .map(user -> ResponseEntity
                            .ok()
                            .cacheControl(CacheControl.maxAge(30, SECONDS))
                            .body(new UserInRoleRepresentation(user, roleName)))
                    .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}
