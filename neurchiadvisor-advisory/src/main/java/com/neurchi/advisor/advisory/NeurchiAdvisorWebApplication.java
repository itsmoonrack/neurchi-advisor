package com.neurchi.advisor.advisory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@EnableOAuth2Client
@EnableOAuth2Sso
@SpringBootApplication
//@ImportResource({"classpath:application-web-security.xml"})
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

}

