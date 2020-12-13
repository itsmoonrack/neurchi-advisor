package com.neurchi.advisor.identityaccess.domain.model.identity;

import com.neurchi.advisor.identityaccess.domain.model.IdentityAccessTest;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRepositoryTest extends IdentityAccessTest {

    @Test
    public void TestAddUser() {

        User user = this.userAggregate();

        userRepository().add(user);

        assertTrue(userRepository().userWithUsername(user.tenantId(), user.username()).isPresent());
    }

    @Test
    public void TestFindUserByUsername() {

        User user = this.userAggregate();

        userRepository().add(user);

        assertTrue(userRepository().userWithUsername(user.tenantId(), user.username()).isPresent());
    }

    @Test
    public void TestRemoveUser() {

        User user = this.userAggregate();

        userRepository().add(user);

        assertTrue(userRepository().userWithUsername(user.tenantId(), user.username()).isPresent());

        userRepository().remove(user);

        assertTrue(userRepository().userWithUsername(user.tenantId(), user.username()).isEmpty());
    }

    @Test
    public void TestFindSimilarNamedUsers() {

        User user = this.userAggregate();
        userRepository().add(user);

        User user2 = this.userAggregate2();
        userRepository().add(user2);

        FullName name = user.person().name();

        Stream<User> users = userRepository().allSimilarlyNamedUsers(
                user.tenantId(),
                "",
                name.lastName().substring(0, 2));

        assertEquals(2, users.count());
    }
}
