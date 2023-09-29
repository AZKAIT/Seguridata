package com.seguridata.tools.dbmigrator.business.factory;

import com.seguridata.tools.dbmigrator.business.query.DBQueryResolver;
import com.seguridata.tools.dbmigrator.business.query.annotation.DatabaseTypeBean;
import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class QueryResolverFactory {

    private final ApplicationContext appContext;

    private Map<DatabaseType, DBQueryResolver> dbQueryResolvers;

    @Autowired
    public QueryResolverFactory(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @PostConstruct
    public void initialize() {
        this.dbQueryResolvers = this.appContext.getBeansWithAnnotation(DatabaseTypeBean.class)
                .entrySet().stream()
                .filter(entry -> entry.getValue() instanceof DBQueryResolver)
                .map(entry -> {
                    DatabaseTypeBean dbTypeBean = this.appContext
                            .findAnnotationOnBean(entry.getKey(), DatabaseTypeBean.class);

                    return new AbstractMap.SimpleEntry<>(dbTypeBean.dbType(), (DBQueryResolver) entry.getValue());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, () -> new EnumMap<>(DatabaseType.class)));
    }

    public DBQueryResolver getDBQueryResolver(DatabaseType databaseType) {
        if (!this.dbQueryResolvers.containsKey(databaseType)) {
            throw new RuntimeException("No implementation for DB: " + databaseType.name());
        }

        return this.dbQueryResolvers.get(databaseType);
    }
}
