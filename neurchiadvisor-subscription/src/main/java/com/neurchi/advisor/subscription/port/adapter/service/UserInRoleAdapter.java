package com.neurchi.advisor.subscription.port.adapter.service;

import com.neurchi.advisor.subscription.domain.model.collaborator.Collaborator;
import com.neurchi.advisor.subscription.domain.model.group.GroupId;

public interface UserInRoleAdapter {

    <T extends Collaborator> T toCollaborator(
            GroupId groupId,
            String identity,
            String roleName,
            Class<T> collaboratorClass);

}
