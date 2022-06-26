package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponentsBuilder;

import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_JSON_VALUE;
import static com.neurchi.advisor.common.media.NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Controller
@RequestMapping(value = "/tenants", produces = {ID_ADVISOR_TYPE_JSON_VALUE, ID_ADVISOR_TYPE_XML_VALUE})
public class TenantResource extends AbstractResource {

    @PostMapping(value = "{tenantId}/invitations", consumes = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> offerRegistrationInvitation(
            @PathVariable final String tenantId,
            @RequestBody final String description,
            final UriComponentsBuilder builder) {

        final String invitationId =
                this.identityApplicationService()
                        .offerLimitedRegistrationInvitation(
                                new TenantId(tenantId),
                                description);

        return ResponseEntity
                .created(builder
                        .pathSegment("tenants")
                        .pathSegment(tenantId)
                        .pathSegment("invitations")
                        .pathSegment(invitationId)
                        .build().toUri())
                .build();
    }
}
