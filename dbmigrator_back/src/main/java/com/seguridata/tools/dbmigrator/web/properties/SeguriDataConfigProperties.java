package com.seguridata.tools.dbmigrator.web.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "seguri-data.configs")
@Getter @Setter
public class SeguriDataConfigProperties {
    private List<LinkProperty> links;
}
