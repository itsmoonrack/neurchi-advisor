package com.neurchi.advisor.identityaccess.domain.model;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith({SpringExtension.class})
@ContextConfiguration({
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess.xml"})
public abstract class DomainTest {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session session() {
        return this.sessionFactory.getCurrentSession();
    }

    @BeforeEach
    protected void setUp() {
        DomainEventPublisher.instance().reset();
    }
}
