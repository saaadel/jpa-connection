package saaadel.jpa.connection;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

public interface JpaUnwrappedConnection {

    /**
     * Do whatever you need to do with the connection.
     *
     * @param entityManager
     * @param work
     * @throws SQLException
     */
    void doWork(final EntityManager entityManager, Consumer<Connection> work) throws SQLException;
}
