package com.neurchi.advisor.advisory;

import com.neurchi.advisor.common.domain.model.process.TimeConstrainedProcessTrackerRepository;
import com.neurchi.advisor.common.port.adapter.persistence.hibernate.HibernateTimeConstrainedProcessTrackerRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@EnableScheduling
@SpringBootApplication
@ImportResource({
        "classpath:application-context-advisory.xml",
        "classpath:application-web-security.xml"})
public class NeurchiAdvisorWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeurchiAdvisorWebApplication.class, args);
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.FRANCE);
        return sessionLocaleResolver;
    }

    @Bean
    public TimeConstrainedProcessTrackerRepository processTrackerRepository() {
        return new HibernateTimeConstrainedProcessTrackerRepository();
    }
}

