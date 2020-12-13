package com.neurchi.advisor.identityaccess;

import com.neurchi.advisor.identityaccess.application.IdentityApplicationService;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
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
//                                "Desc",
//                                "sbernard",
//                                "Sylvain",
//                                "Bernard",
//                                "accessToken",
//                                "bearer",
//                                LocalDateTime.now().plusDays(90),
//                                "sylvain@neurchiadvisor.com"));
    }
}
