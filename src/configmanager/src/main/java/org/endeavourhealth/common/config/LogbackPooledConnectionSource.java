package org.endeavourhealth.common.config;

import ch.qos.logback.core.db.DriverManagerConnectionSource;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class LogbackPooledConnectionSource extends DriverManagerConnectionSource {

    private HikariDataSource dataSource = null;

    public LogbackPooledConnectionSource() {
        super();
    }


    @Override
    public Connection getConnection() throws SQLException {

        if (dataSource == null) {
            createConnectionPool();
        }

        return dataSource.getConnection();
    }

    private synchronized void createConnectionPool() {
        if (dataSource != null) {
            return;
        }

        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(getUrl());
        dataSource.setUsername(getUser());
        dataSource.setPassword(getPassword());
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setIdleTimeout(60000);
        dataSource.setPoolName("LogbackDBConnectionPool");
    }

}
