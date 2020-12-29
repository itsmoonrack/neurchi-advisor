package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.common.media.RepresentationReader;
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserResourceTest extends ResourceTest {

    @Test
    public void TestIsUserInRole() throws Exception {
        User user = this.userAggregate();
        userRepository().add(user);

        Role role = this.roleAggregate();
        role.assignUser(user);
        roleRepository().add(role);

        String url = "http://localhost:" + this.port() + "/tenants/{tenantId}/users/{username}/inRole/{role}";

        ResponseEntity<String> response =
                this.restTemplate()
                        .getForEntity(
                                url,
                                String.class,
                                user.tenantId().id(),
                                user.username(),
                                role.name());

        assertEquals(HttpStatus.OK, response.getStatusCode());

        RepresentationReader reader = new RepresentationReader(response.getBody());
        assertEquals(user.username(), reader.stringValue("username"));
        assertEquals(role.name(), reader.stringValue("role"));
    }
}