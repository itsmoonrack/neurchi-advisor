package com.neurchi.advisor.subscription.port.adapter.service;

import com.neurchi.advisor.subscription.domain.model.collaborator.Administrator;
import com.neurchi.advisor.subscription.domain.model.collaborator.CollaboratorService;
import com.neurchi.advisor.subscription.domain.model.collaborator.Moderator;
import com.neurchi.advisor.subscription.domain.model.collaborator.Participant;
import com.neurchi.advisor.subscription.domain.model.group.GroupId;
import org.springframework.stereotype.Service;

@Service
public class TranslatingCollaboratorService implements CollaboratorService {

    private final UserInRoleAdapter userInRoleAdapter;

    TranslatingCollaboratorService(final UserInRoleAdapter userInRoleAdapter) {
        this.userInRoleAdapter = userInRoleAdapter;
    }

    @Override
    public Moderator moderatorFrom(final GroupId groupId, final String identity) {
        return this.userInRoleAdapter()
                .toCollaborator(
                        groupId,
                        identity,
                        "Moderator",
                        Moderator.class);
    }

    @Override
    public Administrator administratorFrom(final GroupId groupId, final String identity) {
        return this.userInRoleAdapter()
                .toCollaborator(
                        groupId,
                        identity,
                        "Administrator",
                        Administrator.class);
    }

    @Override
    public Participant participantFrom(final GroupId groupId, final String identity) {
        return this.userInRoleAdapter()
                .toCollaborator(
                        groupId,
                        identity,
                        "Participant",
                        Participant.class);
    }

    private UserInRoleAdapter userInRoleAdapter() {
        return this.userInRoleAdapter;
    }
}
