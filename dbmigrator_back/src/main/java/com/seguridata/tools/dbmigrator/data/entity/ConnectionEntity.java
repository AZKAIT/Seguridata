package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.DatabaseType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "connections")
@Getter @Setter
public class ConnectionEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private DatabaseType type;
    private Boolean locked;

    @ReadOnlyProperty
    @DocumentReference(lookup="{'connection':?#{#self._id} }")
    private List<TableEntity> tables;
}
