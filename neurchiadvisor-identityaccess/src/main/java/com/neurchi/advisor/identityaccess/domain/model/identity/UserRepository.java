package com.neurchi.advisor.identityaccess.domain.model.identity;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserRepository {

    void add(User user);

    Stream<User> allSimilarlyNamedUsers(TenantId tenantId, String firstNamePrefix, String lastNamePrefix);

    void remove(User user);

    Optional<User> userWithUsername(TenantId tenantId, String username);
}
