package com.seguridata.tools.dbmigrator.business.function.impl;

import com.seguridata.tools.dbmigrator.business.function.ConversionFunction;
import com.seguridata.tools.dbmigrator.business.function.annotation.ConversionFunctionBinding;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;


@ConversionFunctionBinding(functionType = ConversionFunctionType.NONE)
public class NoneConversionFunction implements ConversionFunction {

    /**
     * None Conversion Function should return same Object as is.
     *
     * @param input the function argument
     * @return The same Object that was provided
     */
    @Override
    public Object apply(Object input) {
        return input;
    }
}
