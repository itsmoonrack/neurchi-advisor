package com.neurchi.advisor.common.port.adapter.persistence;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProvider {

    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public static void closeConnection() {

        try {
            Connection connection = connection();

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to close connection: " + e.getMessage(), e);
        } finally {
            connectionHolder.set(null);
        }
    }

    public static Connection connection() {
        return connectionHolder.get();
    }

    public static Connection connection(final DataSource dataSource) {

        Connection connection = connection();

        try {
            if (connection == null) {
                connection = dataSource.getConnection();

                connectionHolder.set(connection);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Connection not provided: " + e.getMessage(), e);
        }

        return connection;
    }
}
