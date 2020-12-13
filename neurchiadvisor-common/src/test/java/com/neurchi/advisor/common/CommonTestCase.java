package com.neurchi.advisor.common;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.spring.SpringHibernateSessionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class CommonTestCase {

    protected ApplicationContext applicationContext;
    protected SpringHibernateSessionProvider sessionProvider;
    private Transaction transaction;

    protected Session session() {
        return this.sessionProvider.session();
    }

    protected Transaction transaction() {
        return this.transaction;
    }

    @BeforeEach
    protected void setUp() throws Exception {

        DomainEventPublisher.instance().reset();

        this.applicationContext = new ClassPathXmlApplicationContext("application-context-common.xml");

        this.sessionProvider = this.applicationContext.getBean("sessionProvider", SpringHibernateSessionProvider.class);

        this.setTransaction(this.session().beginTransaction());
    }

    @AfterEach
    protected void tearDown() throws Exception {

        this.transaction().rollback();

        this.setTransaction(null);

        this.session().clear();
    }

    private void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }

}
