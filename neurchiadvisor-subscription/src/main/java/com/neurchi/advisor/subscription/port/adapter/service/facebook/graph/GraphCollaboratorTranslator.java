package com.neurchi.advisor.subscription.port.adapter.service.facebook.graph;

import com.neurchi.advisor.common.port.adapter.service.facebook.types.UserGroup;
import com.neurchi.advisor.subscription.domain.model.collaborator.Administrator;
import com.neurchi.advisor.subscription.domain.model.collaborator.Collaborator;
import com.restfb.DefaultJsonMapper;
import com.restfb.JsonMapper;
import com.restfb.types.User;

import java.lang.reflect.Constructor;

public class GraphCollaboratorTranslator {

    public <T extends Collaborator> T toCollaboratorFromRepresentation(
            final String userRepresentation,
            final String groupRepresentation,
            final Class<T> collaboratorClass)
    throws Exception {

        final JsonMapper mapper = new DefaultJsonMapper();

        final User user = mapper.toJavaObject(userRepresentation, User.class);
        final UserGroup userGroup = mapper.toJavaObject(groupRepresentation, UserGroup.class);

        if (Administrator.class.isAssignableFrom(collaboratorClass)) {
            if (userGroup.getMemberCount() == null) {
                throw new IllegalStateException("User: " + user.getName() + " is not an administrator of: " + userGroup.getName());
            }
        }

        final T collaborator =
                this.newCollaborator(
                        user.getId(),
                        user.getName(),
                        user.getPicture().getUrl(),
                        collaboratorClass);

        return collaborator;
    }

    private <T extends Collaborator> T newCollaborator(
            final String identity,
            final String name,
            final String picture,
            final Class<T> collaboratorClass)
    throws Exception {

        final Constructor<T> constructor =
                collaboratorClass.getConstructor(
                        String.class, String.class, String.class);

        return constructor.newInstance(
                identity,
                name.trim(),
                picture);
    }

}
