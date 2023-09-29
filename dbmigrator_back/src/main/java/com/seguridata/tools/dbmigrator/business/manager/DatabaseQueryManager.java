package com.seguridata.tools.dbmigrator.business.manager;

import com.seguridata.tools.dbmigrator.business.factory.DatabaseConnectionFactory;
import com.seguridata.tools.dbmigrator.business.factory.QueryResolverFactory;
import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.TableService;
import com.seguridata.tools.dbmigrator.data.entity.ConnectionEntity;
import com.seguridata.tools.dbmigrator.data.entity.DefinitionEntity;
import com.seguridata.tools.dbmigrator.data.entity.TableEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DatabaseQueryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseQueryManager.class);

    private final ConnectionService connectionService;
    private final TableService tableService;

    private ConnectionEntity connection;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;
    private DBQueryResolver queryResolver;
    private boolean initialized;

    private final ApplicationContext appContext;

    public DatabaseQueryManager(ApplicationContext appContext) {
        LOGGER.info("Creating DatabaseQueryManager with: ");
        this.appContext = appContext;
        this.jdbcTemplate = null;
        this.initialized = false;

        this.connectionService = this.appContext.getBean(ConnectionService.class);
        this.tableService = this.appContext.getBean(TableService.class);
    }

    public void initializeConnection(String connectionId) throws SQLException {
        LOGGER.info("Initializing Bean");
        DatabaseConnectionFactory connectionFactory = this.appContext.getBean(DatabaseConnectionFactory.class);
        QueryResolverFactory queryResolverFactory = this.appContext.getBean(QueryResolverFactory.class);
        this.connection = this.connectionService.getConnection(connectionId);
        this.queryResolver = queryResolverFactory.getDBQueryResolver(this.connection.getType());

        DataSource dataSource = connectionFactory.getConnection(this.connection);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.jdbcTemplate.execute(this.queryResolver.verificationQuery());

        this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.initialized = true;
    }

    public List<Map<String, Object>> retrieveDataBlockFrom(TableEntity sourceTable, List<DefinitionEntity> sourceDefinitions) {
        String query = this.queryResolver.selectFromSourceTableQuery(sourceTable, sourceDefinitions);
        return this.jdbcTemplate.queryForList(query);
    }

    public Map<String, Object> insertDataBlockTo(TableEntity targetTable, List<DefinitionEntity> targetDefinitions, Map<String, Object> data) {
        String query = this.queryResolver.insertToTargetTableQuery(targetTable, targetDefinitions);
        SqlParameterSource parameters = this.createParameters(targetDefinitions, data);

        return this.namedParamJdbcTemplate.queryForMap(query, parameters);
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
