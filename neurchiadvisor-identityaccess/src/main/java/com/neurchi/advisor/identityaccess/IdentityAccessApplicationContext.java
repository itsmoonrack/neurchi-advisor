package com.neurchi.advisor.identityaccess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@EnableAutoConfiguration
@ImportResource({
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess.xml",
        "classpath:application-context-identityaccess-application.xml"})
public class IdentityAccessApplicationContext {

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessApplicationContext.class, args);
    }
}

