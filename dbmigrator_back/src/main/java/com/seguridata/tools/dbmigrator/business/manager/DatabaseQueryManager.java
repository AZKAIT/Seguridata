package com.seguridata.tools.dbmigrator.business.manager;

import com.seguridata.tools.dbmigrator.business.factory.DatabaseConnectionFactory;
import com.seguridata.tools.dbmigrator.business.factory.QueryResolverFactory;
import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.mapper.ColumnRowMapper;
import com.seguridata.tools.dbmigrator.data.mapper.TableRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatabaseQueryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseQueryManager.class);

    private final ConnectionService connectionService;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;
    private DBQueryResolver queryResolver;
    private ConnectionEntity connection;
    private boolean initialized;

    private final ApplicationContext appContext;

    public DatabaseQueryManager(ApplicationContext appContext) {
        LOGGER.info("Creating DatabaseQueryManager with: ");
        this.appContext = appContext;
        this.jdbcTemplate = null;
        this.initialized = false;

        this.connectionService = this.appContext.getBean(ConnectionService.class);
    }

    public void initializeConnection(String connectionId) throws SQLException {
        ConnectionEntity connection = this.connectionService.getConnection(connectionId);
        this.initializeConnection(connection);
    }

    public void initializeConnection(ConnectionEntity connection) throws SQLException {
        LOGGER.info("Initializing Bean");
        DatabaseConnectionFactory connectionFactory = this.appContext.getBean(DatabaseConnectionFactory.class);
        QueryResolverFactory queryResolverFactory = this.appContext.getBean(QueryResolverFactory.class);
        this.connection = connection;
        this.queryResolver = queryResolverFactory.getDBQueryResolver(this.connection.getType());

        DataSource dataSource = connectionFactory.getConnection(this.connection);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.execute(this.queryResolver.verificationQuery());

        this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.initialized = true;
    }

    public List<TableEntity> findSchemaTables() {
        String query = this.queryResolver.getTablesQuery();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("namespace", this.connection.getDatabase());

        return this.namedParamJdbcTemplate.query(query, namedParameters, new TableRowMapper());
    }

    public List<ColumnEntity> findColumnForTable(TableEntity table) {
        String query = this.queryResolver.getColumnsQuery();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("tableName", table.getName());
        namedParameters.addValue("schema", table.getSchema());
        namedParameters.addValue("namespace", this.connection.getDatabase());

        return this.namedParamJdbcTemplate.query(query, namedParameters, new ColumnRowMapper());
    }

    public long getTotalRows(TableEntity table) {
        String query = this.queryResolver.countQuery();
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("schema", table.getSchema());
        namedParameters.addValue("tableName", table.getName());

        BigDecimal result = this.namedParamJdbcTemplate.queryForObject(query, namedParameters, BigDecimal.class);
        if (Objects.isNull(result)) {
            throw new DataAccessResourceFailureException("Total Rows is null");
        }
        return result.longValue();
    }

    public List<Map<String, Object>> retrieveDataBlockFrom(TableEntity sourceTable, List<DefinitionEntity> sourceDefinitions, long skip, long limit) {
        String query = this.queryResolver.selectFromSourceTableQuery(sourceTable, sourceDefinitions, skip, limit);
        return this.jdbcTemplate.queryForList(query);
    }

    public int insertDataBlockTo(TableEntity targetTable, List<DefinitionEntity> targetDefinitions, Map<String, Object> data) {
        String query = this.queryResolver.insertToTargetTableQuery(targetTable, targetDefinitions);
        SqlParameterSource parameters = this.createParameters(targetDefinitions, data);

        return this.namedParamJdbcTemplate.update(query, parameters);
    }


    private SqlParameterSource createParameters(List<DefinitionEntity> targetDefinitions, Map<String, Object> data) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        targetDefinitions.forEach(definition -> {
            String paramName = definition.getTargetColumn().getName();
            namedParameters.addValue(paramName, data.get(paramName));
        });
        return namedParameters;
    }
}
