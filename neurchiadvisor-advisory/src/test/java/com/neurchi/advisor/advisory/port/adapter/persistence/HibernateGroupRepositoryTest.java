package com.neurchi.advisor.advisory.port.adapter.persistence;

import com.neurchi.advisor.advisory.domain.model.group.Group;
import com.neurchi.advisor.advisory.domain.model.group.GroupId;
import com.neurchi.advisor.advisory.domain.model.group.GroupRepository;
import com.neurchi.advisor.advisory.domain.model.subscription.SubscriptionAvailability;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerId;
import com.neurchi.advisor.advisory.domain.model.tenant.TenantId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class HibernateGroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Test
    public void TestSave() {
        TenantId tenantId = new TenantId("T-12345");

        Group group =
                new Group(
                        tenantId,
                        new GroupId("298448690577160"),
                        new GroupOwnerId(tenantId, "10157329112631392"),
                        "Neurchi de Finances: The Bottom Fishing Club",
                        "Where we fish in deep water",
                        LocalDateTime.of(2017, 4, 19, 10, 59),
                        971,
                        "cover-picture.jpg",
                        SubscriptionAvailability.NotRequested);

        groupRepository.add(group);

        Group savedGroup =
                groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElse(null);

        assertNotNull(savedGroup);
        assertEquals(group.tenantId(), savedGroup.tenantId());
        assertEquals(group.groupId(), savedGroup.groupId());
        assertEquals(group.groupOwnerId(), savedGroup.groupOwnerId());
        assertEquals(group.name(), savedGroup.name());
        assertEquals("Neurchi de Finances: The Bottom Fishing Club", savedGroup.name());
        assertEquals("Where we fish in deep water", savedGroup.description());
        assertEquals(LocalDateTime.of(2017, 4, 19, 10, 59), savedGroup.createdOn());
        assertEquals(971, savedGroup.memberCount());
        assertEquals("cover-picture.jpg", savedGroup.cover());
        assertEquals(SubscriptionAvailability.NotRequested, savedGroup.subscription().availability());

        Stream<Group> savedGroups =
                groupRepository.allGroupsOfTenant(group.tenantId());

        assertNotNull(savedGroups);
        assertEquals(1, savedGroups.count());
    }

    @Test
    public void TestStartSubscriptionInitiationSave() {
        TenantId tenantId = new TenantId("T-12345");

        Group group =
                new Group(
                        tenantId,
                        new GroupId("298448690577160"),
                        new GroupOwnerId(tenantId, "10157329112631392"),
                        "Neurchi de Finances: The Bottom Fishing Club",
                        "Where we fish in deep water",
                        LocalDateTime.of(2017, 4, 19, 10, 59),
                        971,
                        "cover-picture.jpg",
                        SubscriptionAvailability.NotRequested);

        group.startSubscriptionInitiation("ABCDEFGHIJ");

        groupRepository.add(group);

        Group savedGroup =
                groupRepository
                        .groupOfId(
                                group.tenantId(),
                                group.groupId())
                        .orElse(null);

        assertNotNull(savedGroup);
        assertEquals(group.tenantId(), savedGroup.tenantId());
        assertEquals(group.groupId(), savedGroup.groupId());
        assertEquals(group.groupOwnerId(), savedGroup.groupOwnerId());
        assertEquals(group.name(), savedGroup.name());
        assertEquals("Neurchi de Finances: The Bottom Fishing Club", savedGroup.name());
        assertEquals("Where we fish in deep water", savedGroup.description());
        assertEquals(LocalDateTime.of(2017, 4, 19, 10, 59), savedGroup.createdOn());
        assertEquals(971, savedGroup.memberCount());
        assertEquals("cover-picture.jpg", savedGroup.cover());
        assertEquals(SubscriptionAvailability.NotRequested, savedGroup.subscription().availability());
    }

    @Test
    public void TestRemove() {
        TenantId tenantId = new TenantId("T-12345");

        Group group1 =
                new Group(
                        tenantId,
                        new GroupId("298448690577160"),
                        new GroupOwnerId(tenantId, "10157329112631392"),
                        "Neurchi de Finances: The Bottom Fishing Club",
                        "Where we fish in deep water",
                        LocalDateTime.of(2017, 4, 19, 10, 59),
                        971,
                        "cover-picture.jpg",
                        SubscriptionAvailability.NotRequested);

        Group group2 =
                new Group(
                        tenantId,
                        new GroupId("114485992455090"),
                        new GroupOwnerId(tenantId, "10157329112631392"),
                        "Neurchi de Cuisine",
                        "French mad at food",
                        LocalDateTime.of(2017, 5, 6, 12, 00),
                        2451,
                        "cover-picture.jpg",
                        SubscriptionAvailability.NotRequested);

        groupRepository.add(group1);
        groupRepository.add(group2);

        groupRepository.remove(group1);

        Collection<Group> savedGroups = groupRepository.allGroupsOfTenant(tenantId).collect(Collectors.toList());
        assertFalse(savedGroups.isEmpty());
        assertEquals(1, savedGroups.size());
        assertEquals(group2.groupId(), savedGroups.iterator().next().groupId());

        groupRepository.remove(group2);

        savedGroups = groupRepository.allGroupsOfTenant(tenantId).collect(Collectors.toList());
        assertTrue(savedGroups.isEmpty());
    }
}
