import com.neurchi.advisor.common.port.adapter.persistence.hibernate.AbstractHibernateSession;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import org.hibernate.query.Query;

import java.util.List;

public class FacebookUserService extends AbstractHibernateSession {

    private final UserAdapter userAdapter = null;
    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final GroupMemberService groupMemberService;

    public FacebookUserService(
//            final UserAdapter userAdapter,
            final GroupRepository groupRepository,
            final RoleRepository roleRepository,
            final GroupMemberService groupMemberService) {

//        this.userAdapter = userAdapter;
        this.roleRepository = roleRepository;
        this.groupRepository = groupRepository;
        this.groupMemberService = groupMemberService;
    }

    public void maintainUsers(final Tenant tenant) {
        Query<User> query = this.session().createQuery("from User where tenantId = ?1 ", User.class);

        query.setParameter(1, tenant.tenantId());

        query.stream().forEach(user -> {

            if (user.accessToken().isShortLived()) {
                final AccessToken accessToken =
                        this.userAdapter
                                .extendAccessToken(
                                        user.accessToken());

                user.extendAccessToken(accessToken);
            }

            final UserAdapter.PersonDescriptor descriptor =
                    this.userAdapter
                            .toPersonDescriptor(
                                    user.accessToken());

            user.changePersonalName(descriptor.fullName());
            descriptor.emailAddress().map(ContactInformation::new).ifPresent(user::changePersonalContactInformation);

            final List<UserAdapter.UserInGroup> userInGroups =
                    this.userAdapter
                            .toGroupsDescriptor(
                                    user.accessToken());

            for (UserAdapter.UserInGroup userInGroup : userInGroups) {
                final Group group =
                        this.groupRepository
                                .groupNamed(tenant.tenantId(), userInGroup.id())
                                .orElse(null);

                if (group != null) {
                    group.addUser(user);

                    if (userInGroup.isAdministrator()) {
                        final Role role =
                                this.roleRepository
                                        .roleNamed(tenant.tenantId(), "Administrator-For-" + group.name())
                                        .orElseGet(() -> tenant.provisionRole("Administrator-For-" + group.name(), "Administrators for " + group.name()));

                        if (!role.isInRole(user, this.groupMemberService)) {
                            role.assignUser(user);
                        }
                    }
                }
            }
        });
    }
}
