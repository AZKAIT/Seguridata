package com.seguridata.tools.dbmigrator.business.function.annotation;

import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ConversionFunctionBinding {
    ConversionFunctionType functionType();
}
