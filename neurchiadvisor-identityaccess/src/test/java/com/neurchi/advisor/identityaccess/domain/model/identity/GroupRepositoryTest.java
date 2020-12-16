package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GroupRepositoryTest extends IdentityAccessTest {

    @Test
    public void TestRemoveGroupReferencedUser() {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        User user = this.userAggregate();
        userRepository().add(user);
        groupA.addUser(user);
        groupRepository().add(groupA);

        assertEquals(1, groupA.groupMembers().size());
        assertTrue(groupA.isMember(user, groupMemberService()));

        userRepository().remove(user);
        this.session().flush();
        this.session().evict(groupA);

        Group reGrouped = groupRepository()
                .groupNamed(tenant.tenantId(), "GroupA")
                .orElse(null);

        assertNotNull(reGrouped);
        assertEquals("GroupA", reGrouped.name());
        assertEquals(1, reGrouped.groupMembers().size());
        assertFalse(reGrouped.isMember(user, groupMemberService()));
    }

    @Test
    public void TestRepositoryRemoveGroup() {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        groupRepository().add(groupA);

        Optional<Group> notNullGroup =
                groupRepository()
                        .groupNamed(tenant.tenantId(), "GroupA");
        assertTrue(notNullGroup.isPresent());

        groupRepository().remove(groupA);

        Optional<Group> nullGroup =
                groupRepository()
                        .groupNamed(tenant.tenantId(), "GroupA");
        assertTrue(nullGroup.isEmpty());
    }
}
