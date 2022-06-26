package com.neurchi.advisor.advisory.port.adapter.web;

import com.neurchi.advisor.advisory.port.adapter.service.IdentityAccessTenantsAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@RequestMapping("/login/facebook")
public final class LoginFacebookController {

    private final String tenantId = "A0B0EE7F-B85A-4040-BB73-B686CF5994CA";
    private final UriComponentsBuilder builder;
    private final IdentityAccessTenantsAdapter identityAccessTenantsAdapter;

    public LoginFacebookController(final IdentityAccessTenantsAdapter identityAccessTenantsAdapter) {
        this.identityAccessTenantsAdapter = identityAccessTenantsAdapter;
        this.builder =
                UriComponentsBuilder.newInstance()
                        .scheme("https")
                        .host("www.facebook.com")
                        .path("/v9.0")
                        .path("/dialog/oauth")
                        .queryParam("client_id", "288497445368675");
    }

    @RequestMapping(method = GET)
    public String login(final UriComponentsBuilder builder) {

        final String invitationId =
                this.identityAccessTenantsAdapter()
                        .offerRegistrationInvitation(
                                tenantId,
                                ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment("callback"));

        return "redirect:" + this.builder()
                .queryParam("redirect_uri", builder.build().toUri())
                .queryParam("state", invitationId)
                .queryParam("scope", "email,groups_access_member_info")
                .build()
                .toUriString();
    }

    @RequestMapping(method = GET, path = "callback/{description}")
    public String callback(
            @PathVariable final String description,
            @RequestParam final String state,
            @RequestParam final Map<String, String> parameters) {

        if (parameters.containsKey("error")) {
            final String error = parameters.get("error");
            final String errorCode = parameters.get("error_code");
            final String errorDescription = parameters.get("error_description");
            final String errorReason = parameters.get("error_reason");

        } else if (parameters.containsKey("code")) {
            final String codeParameter = parameters.get("code");

            this.identityAccessTenantsAdapter()
                    .registerUser(
                            tenantId,
                            ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString(),
                            codeParameter);
        }

        return "apps/group/list-default";
    }

    private UriComponentsBuilder builder() {
        return this.builder.cloneBuilder();
    }

    private IdentityAccessTenantsAdapter identityAccessTenantsAdapter() {
        return this.identityAccessTenantsAdapter;
    }
}
