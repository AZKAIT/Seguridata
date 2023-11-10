package com.seguridata.tools.dbmigrator.business.factory;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.seguridata.tools.dbmigrator.business.exception.DBValidationException;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import com.seguridata.tools.dbmigrator.data.dto.ConnectionTestResult;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class DatabaseConnectionFactory {

    public DataSource getConnection(ConnectionEntity connection) throws SQLException {
        if (DatabaseType.ORACLE.equals(connection.getType())) {
            return this.createOracleDataSource(connection);
        }

        if (DatabaseType.MSSQL.equals(connection.getType())) {
            return this.createMSSqlServerDataSource(connection);
        }

        throw new IllegalArgumentException();
    }

    public ConnectionTestResult testConnection(DataSource dataSource) {
        ConnectionTestResult result = new ConnectionTestResult();
        try(Connection connection = dataSource.getConnection()) {
            result.setSuccessful(Objects.nonNull(connection) && connection.isValid(0));
        } catch (SQLException e) {
            result.setSuccessful(false);
            result.setMessage(e.getMessage());
        }

        return result;
    }

    public ConnectionTestResult createAndTestConnection(ConnectionEntity connection) {
        try {
            DataSource dataSource = this.getConnection(connection);
            return testConnection(dataSource);
        } catch (SQLException e) {
            throw new DBValidationException(e.getMessage());
        }
    }



    private DataSource createOracleDataSource(ConnectionEntity connection) throws SQLException {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(connection.getUsername());
        dataSource.setPassword(connection.getPassword());
        dataSource.setServiceName(connection.getObjectService());
        dataSource.setDatabaseName(connection.getDatabase());
        dataSource.setDriverType("thin");
        dataSource.setServerName(connection.getHost());
        dataSource.setPortNumber(connection.getPort());

        return dataSource;
    }

    private DataSource createMSSqlServerDataSource(ConnectionEntity connection) {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setUser(connection.getUsername());
        ds.setPassword(connection.getPassword());
        ds.setServerName(connection.getHost());
        ds.setPortNumber(connection.getPort());
        ds.setDatabaseName(connection.getDatabase());
        ds.setInstanceName(connection.getObjectService());

        ds.setEncrypt(Boolean.toString(false));

        return ds;
    }
}
