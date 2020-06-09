package com.neurchi.advisor.common.persistence;

import org.hibernate.Session;

public class PersistenceManagerProvider {

    private Session hibernateSession;

    public PersistenceManagerProvider(final Session hibernateSession) {
        this.setHibernateSession(hibernateSession);
    }

    public Session hibernateSession() {
        return this.hibernateSession;
    }

    public boolean hasHibernateSession() {
        return this.hibernateSession() != null;
    }

    protected PersistenceManagerProvider() {
        super();
    }

    private void setHibernateSession(final Session hibernateSession) {
        this.hibernateSession = hibernateSession;
    }
}
