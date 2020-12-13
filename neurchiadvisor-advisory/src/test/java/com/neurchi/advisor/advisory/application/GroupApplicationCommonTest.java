package com.neurchi.advisor.advisory.application;

import com.neurchi.advisor.advisory.application.group.GroupApplicationService;
import com.neurchi.advisor.advisory.domain.model.group.Group;
import com.neurchi.advisor.advisory.domain.model.group.GroupCommonTest;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwner;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;

public abstract class GroupApplicationCommonTest extends GroupCommonTest {

    @Autowired
    protected GroupApplicationService groupApplicationService;

    protected Group persistedGroupForTest() {
        Group group = this.groupForTest();

        groupRepository.add(group);

        return group;
    }

    protected GroupOwner persistedGroupOwnerForTest() {
        GroupOwner groupOwner =
                new GroupOwner(
                        new TenantId("T-12345"),
                        "10157329112631392",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        groupOwnerRepository.add(groupOwner);

        return groupOwner;
    }
}
