package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.application.command.AssignUserToRoleCommand;
import com.neurchi.advisor.identityaccess.application.command.RegisterUserCommand;
import com.neurchi.advisor.identityaccess.application.representation.UserInRoleRepresentation;
import com.neurchi.advisor.identityaccess.application.representation.UserRepresentation;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.UriComponentsBuilder;

import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_JSON_VALUE;
import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_XML_VALUE;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

@Controller
@RequestMapping(value = "/tenants/{tenantId}/users", produces = {ID_ADVISOR_TYPE_JSON_VALUE, ID_ADVISOR_TYPE_XML_VALUE})
public class UserResource extends AbstractResource {

    @PostMapping
    public ResponseEntity<?> registerUser(
            @RequestBody final RegisterUserCommand command,
            final UriComponentsBuilder builder) {

        final User user =
                this.identityApplicationService()
                        .registerUser(command);

        return ResponseEntity
                .created(builder
                        .pathSegment("tenants")
                        .pathSegment(command.getTenantId())
                        .pathSegment("users")
                        .pathSegment(user.username())
                        .build().toUri())
                .build();
    }

    @GetMapping(path = "{username}")
    public ResponseEntity<UserRepresentation> getUser(
            @PathVariable final String tenantId,
            @PathVariable final String username,
            final WebRequest request) {

        return this.identityApplicationService()
                .user(tenantId, username)
                .map(user -> this.userResponse(request, user))
                .orElse(ResponseEntity.notFound().build());
    }

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
                            .cacheControl(this.cacheControlFor(30, SECONDS))
                            .body(new UserInRoleRepresentation(user, roleName)))
                    .orElseGet(() -> ResponseEntity.noContent().build());
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(path = "{username}/inRole/{roleName}")
    public ResponseEntity<UserInRoleRepresentation> assignUserToRole(
            @PathVariable final String tenantId,
            @PathVariable final String username,
            @PathVariable final String roleName) {

        this.accessApplicationService()
                .assignUserToRole(
                        new AssignUserToRoleCommand(
                                tenantId,
                                username,
                                roleName));

        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<UserRepresentation> userResponse(final WebRequest request, final User user) {

        String etag = this.userETag(user);

        if (request.checkNotModified(etag)) {
            return null;
        }

        return ResponseEntity
                .ok()
                .cacheControl(this.cacheControlFor(1, HOURS))
                .eTag(etag)
                .body(new UserRepresentation(user));
    }
}
