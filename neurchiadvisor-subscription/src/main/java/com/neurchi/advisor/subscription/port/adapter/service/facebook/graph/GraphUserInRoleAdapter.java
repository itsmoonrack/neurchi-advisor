package com.neurchi.advisor.subscription.port.adapter.service.facebook.graph;

import com.neurchi.advisor.subscription.domain.model.collaborator.Collaborator;
import com.neurchi.advisor.subscription.domain.model.group.GroupId;
import com.neurchi.advisor.subscription.port.adapter.service.UserInRoleAdapter;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.batch.BatchRequest;
import com.restfb.batch.BatchResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GraphUserInRoleAdapter implements UserInRoleAdapter {

//    @Value("${security.oauth2.client.client-secret}")
    private String appSecret; // TODO: Move this away

    @Override
    public <T extends Collaborator> T toCollaborator(
            final GroupId groupId,
            final String identity,
            final String roleName,
            final Class<T> collaboratorClass) {


        final T collaborator;

        try {
            final SecurityContext context = SecurityContextHolder.getContext();

            if (context.getAuthentication() instanceof OAuth2Authentication authentication) {
                if (authentication.getDetails() instanceof OAuth2AuthenticationDetails details) {

                    final FacebookClient facebookClient = new DefaultFacebookClient(details.getTokenValue(), this.appSecret, Version.VERSION_9_0);

                    final BatchRequest userRequest = new BatchRequest.BatchRequestBuilder("me")
                            .parameters(Parameter.with("fields", "picture.width(40).height(40),name"))
                            .build();

                    final BatchRequest groupRequest = new BatchRequest.BatchRequestBuilder(groupId.id())
                            .parameters(Parameter.with("fields", "member_count,name")) // checks admin rights
                            .build();

                    final List<BatchResponse> response = facebookClient.executeBatch(userRequest, groupRequest);

                    if (response.get(0).getCode() == 200 && response.get(1).getCode() == 200) {
                        collaborator =
                                new GraphCollaboratorTranslator()
                                        .toCollaboratorFromRepresentation(
                                                response.get(0).getBody(),
                                                response.get(1).getBody(),
                                                collaboratorClass);
                    } else {
                        throw new IllegalStateException(
                                "Failed requesting the user: "
                                        + identity
                                        + " in role: "
                                        + roleName
                                        + " for group: "
                                        + groupId +
                                        " with resulting status: "
                                        + response.get(1).getCode()
                                        + ".");
                    }
                } else {
                    throw new IllegalStateException("User is not authenticated.");
                }
            } else {
                throw new IllegalStateException("User is not authenticated.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed requesting the user", e);
        }

        return collaborator;
    }

}
