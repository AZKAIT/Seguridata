package com.seguridata.tools.dbmigrator.web.config;

import com.seguridata.segurilib.cipher.Cipher;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class CryptoConfig {
    @PostConstruct
    public void config() throws Exception {
        Cipher.initCipher(Cipher.CipherAlgorithms._AES);
    }
}
