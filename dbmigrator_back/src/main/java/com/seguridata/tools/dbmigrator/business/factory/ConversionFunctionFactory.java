package com.seguridata.tools.dbmigrator.business.factory;

import com.seguridata.tools.dbmigrator.business.function.ConversionFunction;
import com.seguridata.tools.dbmigrator.business.function.annotation.ConversionFunctionBinding;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.AbstractMap;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class ConversionFunctionFactory {
    private final ApplicationContext appContext;
    private Map<ConversionFunctionType, ConversionFunction> conversionFunctions;

    @Autowired
    public ConversionFunctionFactory(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @PostConstruct
    public void initialize() {
        this.conversionFunctions = this.appContext.getBeansWithAnnotation(ConversionFunctionBinding.class)
                .entrySet().stream()
                .filter(entry -> entry.getValue() instanceof ConversionFunction)
                .map(entry -> {
                    ConversionFunctionBinding conversionFunction = this.appContext
                            .findAnnotationOnBean(entry.getKey(), ConversionFunctionBinding.class);

                    return new AbstractMap.SimpleEntry<>(conversionFunction.functionType(), (ConversionFunction) entry.getValue());
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, () -> new EnumMap<>(ConversionFunctionType.class)));
    }

    public ConversionFunction getConversionFunction(ConversionFunctionType functionType) {
        if (!this.conversionFunctions.containsKey(functionType)) {
            throw new IllegalArgumentException("No implementation for Function: " + functionType.name());
        }

        return this.conversionFunctions.get(functionType);
    }
}
