package com.neurchi.advisor.identityaccess.domain.model.identity;

import org.springframework.stereotype.Service;

@Service
public class GroupMemberService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupMemberService(final UserRepository userRepository, final GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public boolean confirmUser(final Group group, final User user) {
        return userRepository
                .userWithUsername(group.tenantId(), user.username())
                .filter(User::isEnabled)
                .isPresent();
    }

    public boolean isMemberGroup(final Group group, final GroupMember groupMember) {
        return group.groupMembers()
                .filter(GroupMember::isGroup)
                .anyMatch(member -> groupMember.equals(member) || groupRepository
                        .groupNamed(member.tenantId(), member.name())
                        .filter(confirmedGroup -> isMemberGroup(confirmedGroup, groupMember))
                        .isPresent());
    }

    public boolean isUserInNestedGroup(final Group group, final User user) {
        return group.groupMembers()
                .filter(GroupMember::isGroup)
                .anyMatch(member -> groupRepository
                        .groupNamed(member.tenantId(), member.name())
                        .filter(confirmedGroup -> confirmedGroup.isMember(user, this))
                        .isPresent());
    }
}
