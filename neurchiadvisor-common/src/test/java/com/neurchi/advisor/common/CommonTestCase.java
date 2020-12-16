package com.neurchi.advisor.common;

import com.neurchi.advisor.common.domain.model.DomainEventPublisher;
import com.neurchi.advisor.common.spring.SpringHibernateSessionProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@ContextConfiguration("classpath:application-context-common.xml")
public abstract class CommonTestCase {

    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    private SpringHibernateSessionProvider sessionProvider;
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
