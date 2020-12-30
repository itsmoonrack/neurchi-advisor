package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateSession {

    private SessionFactory sessionFactory;

    protected AbstractHibernateSession() {
        super();
    }

    protected Session session() {
        return this.sessionFactory.getCurrentSession();
    }

    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
