package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.team.GroupOwner;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerRepository;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HibernateGroupOwnerRepositoryTest {

    @Autowired
    private GroupOwnerRepository groupOwnerRepository;

    @Test
    public void TestSave() {
        GroupOwner groupOwner =
                new GroupOwner(
                        new TenantId("12345"),
                        "sbernard",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        groupOwnerRepository.add(groupOwner);

        GroupOwner savedGroupOwner =
                groupOwnerRepository.groupOwnerOfIdentity(
                        groupOwner.tenantId(),
                        groupOwner.username())
                .orElse(null);

        assertNotNull(savedGroupOwner);
        assertEquals(groupOwner.tenantId(), savedGroupOwner.tenantId());
        assertEquals(groupOwner.username(), savedGroupOwner.username());
        assertEquals(groupOwner.name(), savedGroupOwner.name());
        assertEquals(groupOwner.firstName(), savedGroupOwner.firstName());
        assertEquals(groupOwner.lastName(), savedGroupOwner.lastName());
        assertEquals(groupOwner.picture(), savedGroupOwner.picture());
        assertEquals(groupOwner.emailAddress(), savedGroupOwner.emailAddress());

        Stream<GroupOwner> savedGroupOwners =
                groupOwnerRepository.allGroupOwnersOfTenant(groupOwner.tenantId());

        assertNotNull(savedGroupOwners);
        assertEquals(1, savedGroupOwners.count());
    }

    @Test
    public void TestRemove() {
        GroupOwner groupOwner1 =
                new GroupOwner(
                        new TenantId("12345"),
                        "sbernard",
                        "Sylvain Bernard",
                        "Sylvain",
                        "Bernard",
                        "picture.png",
                        "sylvain@neurchiadvisor.com",
                        Instant.now());

        GroupOwner groupOwner2 =
                new GroupOwner(
                        new TenantId("12345"),
                        "rgerault",
                        "Raphaël Guérault",
                        "Raphaël",
                        "Guérault",
                        "picture.png",
                        "raphael@neurchiadvisor.com",
                        Instant.now());

        groupOwnerRepository.add(groupOwner1);
        groupOwnerRepository.add(groupOwner2);

        groupOwnerRepository.remove(groupOwner1);

        TenantId tenantId = groupOwner2.tenantId();

        Collection<GroupOwner> savedGroupOwners = groupOwnerRepository.allGroupOwnersOfTenant(tenantId).collect(Collectors.toList());
        assertFalse(savedGroupOwners.isEmpty());
        assertEquals(1, savedGroupOwners.size());
        assertEquals(groupOwner2.username(), savedGroupOwners.iterator().next().username());

        groupOwnerRepository.remove(groupOwner2);

        savedGroupOwners = groupOwnerRepository.allGroupOwnersOfTenant(tenantId).collect(Collectors.toList());
        assertTrue(savedGroupOwners.isEmpty());
    }
}
