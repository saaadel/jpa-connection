package saaadel.jpa.connection.impl;

import saaadel.jpa.connection.JpaUnwrappedConnection;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * Hibernate EntityManager 3.5+
 */
public class HibernateJpa2UnwrappedConnection implements JpaUnwrappedConnection {

    @Override
    public void doWork(EntityManager entityManager, Consumer<Connection> work) throws SQLException {
        org.hibernate.Session hibernateSession = entityManager.unwrap(org.hibernate.Session.class);
        hibernateSession.doWork(new org.hibernate.jdbc.Work() {
            @Override
            public void execute(java.sql.Connection connection) throws SQLException {
                work.accept(connection);
            }
        });
    }
}
