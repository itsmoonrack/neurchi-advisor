package com.neurchi.advisor.identityaccess.resource;

import com.neurchi.advisor.identityaccess.IdentityAccessApplicationTestContext;
import com.neurchi.advisor.identityaccess.application.ApplicationServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(classes = IdentityAccessApplicationTestContext.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class ResourceTest extends ApplicationServiceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    protected int port() {
        return this.port;
    }

    protected TestRestTemplate restTemplate() {
        return this.restTemplate;
    }
}

