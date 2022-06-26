import com.neurchi.advisor.common.media.RepresentationReader;
import com.neurchi.advisor.identityaccess.domain.model.identity.AccessToken;
import com.neurchi.advisor.identityaccess.domain.model.identity.AuthenticationService;
import com.neurchi.advisor.identityaccess.domain.model.identity.Tenant;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

public class FacebookAuthenticationService implements AuthenticationService {

    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(FacebookAuthenticationService.class);

    public FacebookAuthenticationService(final WebClient.Builder webClient) {
        this.webClient = webClient
                .baseUrl("https://graph.facebook.com/v9.0/")
                .build();
    }

    @Override
    public AccessToken exchangeCodeForAccessToken(
            final String appId,
            final String redirectUri,
            final String appSecret,
            final String codeParameter) {

        final ResponseEntity<String> response = this.webClient.get()
                .uri(builder -> builder
                        .path("oauth/access_token")
                        .queryParam("client_id", appId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("client_secret", appSecret)
                        .queryParam("code", codeParameter)
                        .build())
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {

            final RepresentationReader reader =
                    new RepresentationReader(
                            response.getBody());

            final String accessToken = reader.stringValue("access_token");
            final String tokenType = reader.stringValue("token_type");
            final Integer expiresIn = reader.integerValue("expires_in");

            return new AccessToken(accessToken, tokenType, LocalDateTime.now().plusSeconds(expiresIn - 1));
        } else {
            this.logger.error("Unable to log. Need implementation to diagnose.");
        }

        return null;
    }

    @Override
    public AccessToken obtainExtendedToken(final Tenant tenant, final AccessToken accessToken) {
        FacebookClient client = new DefaultFacebookClient(Version.VERSION_9_0);
        return null;
    }
}
