import com.neurchi.advisor.identityaccess.domain.model.identity.AccessToken;
import com.neurchi.advisor.identityaccess.domain.model.identity.EmailAddress;
import com.neurchi.advisor.identityaccess.domain.model.identity.FullName;

import java.util.List;
import java.util.Optional;

public interface UserAdapter {

    AccessToken extendAccessToken(AccessToken accessToken);

    PersonDescriptor toPersonDescriptor(AccessToken accessToken);

    List<UserInGroup> toGroupsDescriptor(AccessToken accessToken);

    interface PersonDescriptor {
        FullName fullName();
        Optional<EmailAddress> emailAddress();
    }

    interface UserInGroup {
        String id();
        boolean isAdministrator();
    }
}
