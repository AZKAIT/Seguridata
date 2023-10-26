package com.seguridata.tools.dbmigrator.business.manager;

import com.seguridata.tools.dbmigrator.business.factory.DatabaseConnectionFactory;
import com.seguridata.tools.dbmigrator.business.factory.QueryResolverFactory;
import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.data.entity.ColumnEntity;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import com.seguridata.tools.dbmigrator.data.mapper.ColumnRowMapper;
import com.seguridata.tools.dbmigrator.data.mapper.TableRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessResourceFailureException;
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
import java.util.Optional;

import static java.lang.Boolean.TRUE;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatabaseQueryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseQueryManager.class);

    private final ConnectionService connectionService;
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;
    private DBQueryResolver queryResolver;
    private ConnectionEntity connection;
    private DataSource dataSource;
    private boolean initialized;

    private final ApplicationContext appContext;

    public DatabaseQueryManager(ApplicationContext appContext) {
        LOGGER.info("Creating DatabaseQueryManager with: ");
        this.appContext = appContext;
        this.initialized = false;

        this.connectionService = this.appContext.getBean(ConnectionService.class);
    }

    public void initializeConnection(String connectionId) throws SQLException {
        this.initializeConnection(this.connectionService.getConnection(connectionId));
    }

    public void initializeConnection(ConnectionEntity connection) throws SQLException {
        LOGGER.info("Initializing Bean");
        DatabaseConnectionFactory connectionFactory = this.appContext.getBean(DatabaseConnectionFactory.class);
        QueryResolverFactory queryResolverFactory = this.appContext.getBean(QueryResolverFactory.class);
        this.connection = connection;
        this.queryResolver = queryResolverFactory.getDBQueryResolver(this.connection.getType());

        this.dataSource = connectionFactory.getConnection(this.connection);

        this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(this.dataSource);
        this.namedParamJdbcTemplate.getJdbcTemplate().execute(this.queryResolver.verificationQuery());

        this.initialized = true;
    }

    public void closeConnection() {
        try {
            this.dataSource.getConnection().close();
        } catch (SQLException e) {
            LOGGER.error("Exception while trying to close DB connection: {}", e.getMessage());
        }
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
        String query = this.queryResolver.countQuery(Optional.ofNullable(table.getSchema()).filter(StringUtils::isNotBlank).map(schema -> String.format("%s.%s", schema, table.getName())).orElse(table.getName()));

        BigDecimal result = this.namedParamJdbcTemplate.queryForObject(query, Collections.emptyMap(), BigDecimal.class);
        if (Objects.isNull(result)) {
            throw new DataAccessResourceFailureException("El número total de filas en la tabla es nulo");
        }
        return result.longValue();
    }

    public List<Map<String, Object>> retrieveDataBlockFrom(TableEntity sourceTable, List<DefinitionEntity> sourceDefinitions, long skip, long limit) {
        String query = this.queryResolver.selectFromSourceTableQuery(sourceTable, sourceDefinitions, skip, limit);
        return this.namedParamJdbcTemplate.queryForList(query, Collections.emptyMap());
    }

    public int insertDataBlockTo(TableEntity targetTable, List<DefinitionEntity> targetDefinitions, Map<String, Object> data) {
        String turnIdentityInsertOn = StringUtils.EMPTY;
        String turnIdentityInsertOff = StringUtils.EMPTY;
        if (this.hasIdentity(targetTable)) {
            turnIdentityInsertOn = this.queryResolver.identityInsertToggleQuery(targetTable, true);
            turnIdentityInsertOff = this.queryResolver.identityInsertToggleQuery(targetTable, false);
        }

        String query = this.queryResolver.insertToTargetTableQuery(targetTable, targetDefinitions);
        query = String.format("%s; %s; %s", turnIdentityInsertOn, query, turnIdentityInsertOff);
        SqlParameterSource parameters = this.createParameters(targetDefinitions, data);

        return this.namedParamJdbcTemplate.update(query, parameters);
    }

    public boolean toggleIdentityInsert(TableEntity targetTable, boolean status) {
        String query = this.queryResolver.identityInsertToggleQuery(targetTable, status);

        this.namedParamJdbcTemplate.update(query, Collections.emptyMap());
        return true;
    }


    private SqlParameterSource createParameters(List<DefinitionEntity> targetDefinitions, Map<String, Object> data) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        targetDefinitions.forEach(definition -> {
            String paramName = definition.getTargetColumn().getName();
            namedParameters.addValue(paramName, data.get(paramName));
        });
        return namedParameters;
    }

    private boolean hasIdentity(TableEntity table) {
        return table.getColumns().stream().map(ColumnEntity::getIdentity).anyMatch(TRUE::equals);
    }
}
