package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import com.seguridata.tools.dbmigrator.web.properties.SeguriDataConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("properties")
public class PropertiesController {
    private final SeguriDataConfigProperties seguriDataConfigProps;

    @Autowired
    public PropertiesController(SeguriDataConfigProperties seguriDataConfigProps) {
        this.seguriDataConfigProps = seguriDataConfigProps;
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<SeguriDataConfigProperties>> getProperties() {
        ResponseWrapper<SeguriDataConfigProperties> propertiesResponse = new ResponseWrapper<>();
        propertiesResponse.setCode("00");
        propertiesResponse.setData(this.seguriDataConfigProps);

        return ResponseEntity.ok(propertiesResponse);
    }
}
