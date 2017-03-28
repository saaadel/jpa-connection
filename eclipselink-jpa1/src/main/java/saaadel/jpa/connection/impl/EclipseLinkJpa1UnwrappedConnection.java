package saaadel.jpa.connection.impl;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.sessions.UnitOfWork;
import saaadel.jpa.connection.JpaUnwrappedConnection;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * EclipseLink <2.0
 */
public class EclipseLinkJpa1UnwrappedConnection implements JpaUnwrappedConnection {

    @Override
    public void doWork(EntityManager entityManager, Consumer<Connection> work) throws SQLException {
        final JpaEntityManager jpaEntityManager = (JpaEntityManager) entityManager.getDelegate();
        final AbstractSession session = (AbstractSession) jpaEntityManager.getActiveSession();
        final UnitOfWork unitOfWork = (UnitOfWork) jpaEntityManager.getActiveSession();
        final Accessor accessor;
        if (session.isInTransaction() || session.isExclusiveIsolatedClientSession()) {
            accessor = session.getAccessor();
        } else {
            unitOfWork.beginEarlyTransaction();
            accessor = session.getAccessor();
            accessor.incrementCallCount(unitOfWork.getParent());
            accessor.decrementCallCount();
        }
        final java.sql.Connection connection = accessor.getConnection();
        work.accept(connection);
    }
}

