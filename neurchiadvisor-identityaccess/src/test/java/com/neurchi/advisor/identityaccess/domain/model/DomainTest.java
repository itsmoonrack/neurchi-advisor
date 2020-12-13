package com.neurchi.advisor.identityaccess.domain.model;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.spring.SpringHibernateSessionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = {
        "classpath:application-context-common.xml",
        "classpath:application-context-identityaccess.xml"})
public abstract class DomainTest {

    @Autowired
    private SpringHibernateSessionProvider sessionProvider;
    private Transaction transaction;

    protected Session session() {
        return this.sessionProvider.session();
    }

    @BeforeEach
    protected void setUp() {

        this.setTransaction(this.session().beginTransaction());

        DomainEventPublisher.instance().reset();
    }

    @AfterEach
    protected void tearDown() {

        this.transaction().rollback();

        this.setTransaction(null);

        this.session().clear();
    }

    protected Transaction transaction() {
        return this.transaction;
    }

    private void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }
}
