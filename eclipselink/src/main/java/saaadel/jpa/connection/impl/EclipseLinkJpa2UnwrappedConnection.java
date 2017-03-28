package saaadel.jpa.connection.impl;

import saaadel.jpa.connection.JpaUnwrappedConnection;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * EclipseLink 2.0+
 */
public class EclipseLinkJpa2UnwrappedConnection implements JpaUnwrappedConnection {

    @Override
    public void doWork(EntityManager entityManager, Consumer<Connection> work) throws SQLException {
        final java.sql.Connection connection = entityManager.unwrap(java.sql.Connection.class);
        work.accept(connection);
    }
}
