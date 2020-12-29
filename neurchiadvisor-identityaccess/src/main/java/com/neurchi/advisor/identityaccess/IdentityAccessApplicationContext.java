package com.neurchi.advisor.identityaccess;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@EnableAutoConfiguration
@ImportResource({
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess.xml",
        "classpath:application-context-identityaccess-application.xml"})
public class IdentityAccessApplicationContext implements ApplicationContextAware {

    public static void main(String[] args) {
        SpringApplication.run(IdentityAccessApplicationContext.class, args);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        IdentityApplicationService identityApplicationService = applicationContext.getBean(IdentityApplicationService.class);
//        identityApplicationService
//                .provisionTenant(
//                        new ProvisionTenantCommand(
//                                "Test",
//                                "Test",
//                                "sbernard",
//                                "Sylvain",
//                                "Bernard",
//                                "token",
//                                "bearer",
//                                LocalDateTime.now().plusDays(90),
//                                "sylvain@neurchi.com"));
    }

    @Bean
    ParameterNamesModule parameterNamesModule() {
        return new ParameterNamesModule(JsonCreator.Mode.PROPERTIES);
    }
}

