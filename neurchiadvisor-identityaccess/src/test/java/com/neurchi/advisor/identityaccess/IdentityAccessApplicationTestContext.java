package com.neurchi.advisor.identityaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@ImportResource({
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess-application.xml",
        "classpath:application-context-identityaccess-test.xml"})
@SpringBootConfiguration
@EnableAutoConfiguration
public class IdentityAccessApplicationTestContext {

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessApplicationTestContext.class, args);
    }

}
