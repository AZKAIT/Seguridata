package com.seguridata.tools.dbmigrator.business.function.impl;

import com.seguridata.segurilib.cipher.Cipher;
import com.seguridata.tools.dbmigrator.business.function.ConversionFunction;
import com.seguridata.tools.dbmigrator.business.function.annotation.ConversionFunctionBinding;
import com.seguridata.tools.dbmigrator.data.constant.ConversionFunctionType;

@ConversionFunctionBinding(functionType = ConversionFunctionType.ENCRYPT)
public class EncryptConversionFunction implements ConversionFunction {

    /**
     * Function to convert the input into String and then encrypt
     * using SeguriData's Cipher library.
     *
     * @param input the data to convert to String and encrypt
     * @return the Encrypted value
     */
    @Override
    public Object apply(Object input) {
        try {
            return Cipher.cipher(input.toString());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
