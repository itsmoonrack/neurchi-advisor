package com.neurchi.advisor.advisory.domain.model;

import com.neurchi.advisor.advisory.domain.model.group.GroupRepository;
import com.neurchi.advisor.advisory.domain.model.team.GroupOwnerRepository;
import com.neurchi.advisor.advisory.domain.model.team.TeamMemberRepository;
import com.neurchi.advisor.advisory.domain.model.team.TeamRepository;
import com.neurchi.advisor.common.domain.model.EventTrackingTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public abstract class DomainTest extends EventTrackingTest {

    @Autowired
    protected GroupRepository groupRepository;
    @Autowired
    protected GroupOwnerRepository groupOwnerRepository;
    @Autowired
    protected TeamRepository teamRepository;
    @Autowired
    protected TeamMemberRepository teamMemberRepository;

    @BeforeEach
    protected void setUp() throws Exception {

        super.setUp();
    }
}
