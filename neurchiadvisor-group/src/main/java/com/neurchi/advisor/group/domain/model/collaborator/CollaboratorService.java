package com.neurchi.advisor.group.domain.model.collaborator;

import com.neurchi.advisor.group.domain.model.tenant.Tenant;

public interface CollaboratorService {

    Moderator moderatorFrom(Tenant tenant, String identity);

    Administrator administratorFrom(Tenant tenant, String identity);

    Participant participantFrom(Tenant tenant, String identity);

}
