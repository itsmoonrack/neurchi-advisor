package com.neurchi.advisor.advisory.port.adapter.service;

import com.neurchi.advisor.common.media.NeurchiAdvisorMediaType;
import com.neurchi.advisor.common.media.RepresentationReader;
import com.neurchi.advisor.common.serializer.PropertiesSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Properties;
import java.util.UUID;

@Service
public class IdentityAccessTenantsAdapter {

    private final WebClient webClient;
    private final PropertiesSerializer serializer;

    public IdentityAccessTenantsAdapter(final WebClient.Builder webClient) {
        this.serializer = PropertiesSerializer.instance();
        this.webClient = webClient
                .baseUrl("http://localhost:9000/tenants/")
                .build();
    }

    public String tenantName(final String tenantId) {
        final ResponseEntity<String> response =
                this.webClient
                        .post()
                        .uri("{tenantId}/invitations", tenantId)
                        .contentType(NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_JSON)
                        .retrieve()
                        .toEntity(String.class)
                        .block();

        if (response != null && response.getStatusCode().is2xxSuccessful()) {
            final RepresentationReader reader
                    = new RepresentationReader(
                            response.getBody());

            return reader.stringValue("name");
        } else {
            throw new IllegalStateException();
        }
    }

    public String offerRegistrationInvitation(final String tenantId, final UriComponentsBuilder builder) {
        // TODO: Synchronize with spring session
        final String description = UUID.randomUUID().toString().toUpperCase();

        final ResponseEntity<String> response =
                this.webClient
                        .post()
                        .uri("{tenantId}/invitations", tenantId)
                        .bodyValue(builder.path(description).cloneBuilder().build().toUriString())
                        .retrieve()
                        .toEntity(String.class)
                        .block();

        if (response != null && response.getStatusCode().equals(HttpStatus.CREATED)) {
            final URI location = response.getHeaders().getLocation();
            if (location != null) {
                final String path = response.getHeaders().getLocation().getPath();
                final String invitationId = path.substring(path.lastIndexOf("/") + 1);
                return invitationId;
            } else {
                throw new IllegalStateException("No Location header in created invitation resource for tenant: " + tenantId);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    public String registerUser(final String tenantId, final String invitationIdentifier, final String codeParameter) {

        final Properties properties = new Properties();
        properties.put("tenantId", tenantId);
        properties.put("invitationIdentifier", invitationIdentifier);
        properties.put("codeParameter", codeParameter);

        final ResponseEntity<String> response =
                this.webClient
                        .post()
                        .uri("{tenantId}/users", tenantId)
                        .contentType(NeurchiAdvisorMediaType.ID_ADVISOR_TYPE_JSON)
                        .bodyValue(this.serializer().serialize(properties))
                        .retrieve()
                        .toEntity(String.class)
                        .block();

        if (response != null && response.getStatusCode().equals(HttpStatus.CREATED)) {
            final URI location = response.getHeaders().getLocation();
            if (location != null) {
                final String path = response.getHeaders().getLocation().getPath();
                final String username = path.substring(path.lastIndexOf("/") + 1);
                return username;
            } else {
                throw new IllegalStateException("No Location header in created user resource for tenant: " + tenantId);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    private PropertiesSerializer serializer() {
        return this.serializer;
    }
}
