package com.neurchi.advisor.common.spring;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SpringHibernateSessionProvider {

    private static final ThreadLocal<Session> sessionHolder = new ThreadLocal<>();

    private SessionFactory sessionFactory;

    public SpringHibernateSessionProvider() {

    }

    public Session session() {
        Session threadBoundSession = sessionHolder.get();

        if (threadBoundSession == null) {
            threadBoundSession = this.sessionFactory.openSession();
            sessionHolder.set(threadBoundSession);
        }

        return threadBoundSession;
    }

    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
