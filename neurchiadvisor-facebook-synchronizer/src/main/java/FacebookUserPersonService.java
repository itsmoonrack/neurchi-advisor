import com.neurchi.advisor.common.media.RepresentationReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;

public class FacebookUserPersonService {

    private final WebClient webClient;
    private final Logger logger = LoggerFactory.getLogger(FacebookUserPersonService.class);

    public FacebookUserPersonService(final WebClient.Builder webClient) {
        this.webClient = webClient
                .baseUrl("https://graph.facebook.com/v9.0/")
                .build();
    }

    public UserPersonDescriptor personalInformation(
            final TenantId tenantId,
            final AccessToken accessToken) {

        final ResponseEntity<String> response = this.webClient.get()
                .uri("me?fields={fields}", "id,first_name,last_name,email")
                .header(HttpHeaders.AUTHORIZATION, accessToken.asAuthorizationHeader())
                .retrieve()
                .toEntity(String.class)
                .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {

            final RepresentationReader reader =
                    new RepresentationReader(
                            response.getBody());

            final String username = reader.stringValue("id");
            final String firstName = reader.stringValue("first_name");
            final String lastName = reader.stringValue("last_name");
            final Optional<String> emailAddress = reader.optionalStringValue("email");

            final Person person =
                    new Person(
                            tenantId,
                            new FullName(
                                    firstName,
                                    lastName),
                            new ContactInformation(
                                    emailAddress.map(EmailAddress::new).orElse(null)));

            return new UserPersonDescriptor(username, person);
        } else {
            this.logger.error("Unable to log. Need implementation to diagnose.");
        }

        return null;
    }
}
