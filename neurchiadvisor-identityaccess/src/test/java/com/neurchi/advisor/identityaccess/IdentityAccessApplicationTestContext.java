package com.neurchi.advisor.identityaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@ImportResource({
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess-application.xml",
        "classpath:application-context-identityaccess-test.xml"})
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, SecurityAutoConfiguration.class})
public class IdentityAccessApplicationTestContext {

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessApplicationTestContext.class, args);
    }

}
