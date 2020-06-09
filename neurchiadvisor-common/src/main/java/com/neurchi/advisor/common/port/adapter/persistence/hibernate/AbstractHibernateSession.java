package com.neurchi.advisor.common.port.adapter.persistence.hibernate;

import com.neurchi.advisor.common.spring.SpringHibernateSessionProvider;
import org.hibernate.Session;

public abstract class AbstractHibernateSession {

    private Session session;
    private SpringHibernateSessionProvider sessionProvider;

    protected AbstractHibernateSession() {

    }

    protected AbstractHibernateSession(final Session session) {
        this.setSession(session);
    }

    protected Session session() {
        Session actualSession = this.session;

        if (actualSession == null) {
            if (this.sessionProvider == null) {
                throw new IllegalStateException("Requires either a Session or SpringHibernateSessionProvider.");
            }

            actualSession = this.sessionProvider.session();

            // This is not a lazy creation and should not be set on
            // this.session. Setting the session instance assumes that
            // you have used the single argument constructor for a single
            // use. If actualSession is set by the sessionProvider then
            // this instance is for use only by the current thread and
            // must not be retained for subsequent requests.
        }

        return actualSession;
    }

    protected void setSession(final Session session) {
        this.session = session;
    }

    public void setSessionProvider(final SpringHibernateSessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
    }
}
