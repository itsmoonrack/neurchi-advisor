import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.neurchi.advisor.common.media.RepresentationReader;
import com.neurchi.advisor.identityaccess.application.AccessApplicationService;
import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import com.neurchi.advisor.identityaccess.domain.model.identity.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.WeakHashMap;

public class FacebookUserAdapter {

    private final WebClient webClient;
    private final WeakHashMap<String, String> eTagsForMyGroups = new WeakHashMap<>();
    private final WeakHashMap<String, String> eTagsForMe = new WeakHashMap<>();
    private final WeakHashMap<String, Integer> callCount = new WeakHashMap<>();
    private final IdentityApplicationService identityApplicationService;
    private final AccessApplicationService accessApplicationService;
    private final Logger logger = LoggerFactory.getLogger(FacebookUserAdapter.class);

    public FacebookUserAdapter(
            final WebClient.Builder webClient,
            final IdentityApplicationService identityApplicationService,
            final AccessApplicationService accessApplicationService) {

        this.webClient = webClient
                .baseUrl("https://graph.facebook.com/v9.0/")
                .build();

        this.identityApplicationService = identityApplicationService;
        this.accessApplicationService = accessApplicationService;
    }

    public void synchronizeUser(final String tenantId, final String username, final AccessToken accessToken) {

        if (callCount.containsKey(username) && callCount.get(username) > 90) { // factorise
            // TODO: log ? throw ?
            return;
        }

        if (accessToken.isShortLived()) {
            // Obtain Extended Token
            try {
                final String appId = "288497445368675";
                final String secretId = "a093586aa0357e8659d488989487af1a";

                final ResponseEntity<String> response = this.webClient.get()
                        .uri(builder -> builder
                                .path("oauth/access_token")
                                .queryParam("client_id", appId)
                                .queryParam("client_secret", secretId)
                                .queryParam("grant_type", "fb_exchange_token")
                                .queryParam("fb_exchange_token", accessToken.accessToken())
                                .build())
                        .retrieve()
                        .toEntity(String.class)
                        .block();

                if (response != null) {
                    if (response.getStatusCode().is2xxSuccessful()) {

                        final RepresentationReader reader
                                = new RepresentationReader(
                                        response.getBody());

                        final String token = reader.stringValue("access_token");
                        final String tokenType = reader.stringValue("token_type");
                        final LocalDateTime expiresIn = LocalDateTime.ofInstant(reader.instantValue("expires_in"), ZoneId.systemDefault());

                        this.identityApplicationService
                                .extendAccessToken(
                                        new ExtendAccessTokenCommand(
                                                tenantId,
                                                username,
                                                token,
                                                tokenType,
                                                expiresIn));
                    }
                }

            } catch (WebClientResponseException.Unauthorized e) {
                // Token is out of date. We cannot proceed any further API calls until user provides a valid token.
                return;
            }
        }

        // Updates User
        try {
            final ResponseEntity<String> response = this.webClient.get()
                    .uri("me?fields={fields}", "id,first_name,last_name,email")
                    .header(HttpHeaders.AUTHORIZATION, accessToken.asAuthorizationHeader())
                    .header(HttpHeaders.IF_NONE_MATCH, eTagsForMe.get(username))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response != null) {

                if (response.getStatusCode().is2xxSuccessful()) {

                    final RepresentationReader reader =
                            new RepresentationReader(
                                    response.getBody());

                    final String firstName = reader.stringValue("first_name");
                    final String lastName = reader.stringValue("last_name");
                    final String emailAddress = reader.stringValue("email");

                    this.identityApplicationService
                            .changeUserPersonalName(
                                    new ChangeUserPersonalNameCommand(
                                            tenantId,
                                            username,
                                            firstName,
                                            lastName));

                    if (emailAddress != null) {
                        this.identityApplicationService
                                .changeUserEmailAddress(
                                        new ChangeEmailAddressCommand(
                                                tenantId,
                                                username,
                                                emailAddress));
                    }

                    this.eTagsForMe.put(username, response.getHeaders().getETag());

                } else if (!response.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {

                    final RepresentationReader reader =
                            new RepresentationReader(
                                    response.getBody());

                    final String message = reader.stringValue("message");

                    this.logger.error(message);
                }

                this.updateCallCount(username, response.getHeaders());
            }
        } catch (WebClientResponseException.Unauthorized e) {
            // Token is out of date. We cannot proceed any further API calls until user provides a valid token.
            return;
        }

        // Updates Group relationship and Roles
        {
            final ResponseEntity<String> response = this.webClient.get()
                    .uri("me/groups?limit={limit}&fields={fields}", "1000", "id,administrator")
                    .header(HttpHeaders.AUTHORIZATION, accessToken.asAuthorizationHeader())
                    .header(HttpHeaders.IF_NONE_MATCH, eTagsForMyGroups.get(username))
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            if (response != null) {

                if (response.getStatusCode().is2xxSuccessful()) {

                    // TODO: Create a navigable reader (cursors)
                    final RepresentationReader reader
                            = new RepresentationReader(
                                    response.getBody());

                    final ArrayNode groups = reader.array("data");

                    for (JsonNode group : groups) {
                        try {
                            final String groupId = group.path("id").asText();
                            final boolean administrator = group.path("administrator").asBoolean();

                            this.identityApplicationService
                                    .addUserToGroup(
                                            new AddUserToGroupCommand(
                                                    tenantId,
                                                    groupId,
                                                    username));

                            if (administrator) {
                                this.accessApplicationService
                                        .assignUserToRole(
                                                new AssignUserToRoleCommand(
                                                        tenantId,
                                                        username,
                                                        "Administrator-For-" + groupId));
                            }
                        } catch (IllegalArgumentException e) {
                            this.logger.trace("Unable to synchronize me/groups", e);
                        }
                    }

                    this.eTagsForMyGroups.put(username, response.getHeaders().getETag());
                } else if (!response.getStatusCode().equals(HttpStatus.NOT_MODIFIED)) {

                    final RepresentationReader reader =
                            new RepresentationReader(
                                    response.getBody());

                    final String message = reader.stringValue("message");

                    this.logger.error(message);
                }

                this.updateCallCount(username, response.getHeaders());
            }
        }
    }

    private void updateCallCount(final String username, final HttpHeaders headers) {

        final String applicationUsage = headers.getFirst("x-app-usage");

        if (applicationUsage != null) {

            final RepresentationReader reader =
                    new RepresentationReader(
                            applicationUsage);

            final Integer callCount = reader.integerValue("call_count");

            this.callCount.put(username, callCount);
        }
    }
}
