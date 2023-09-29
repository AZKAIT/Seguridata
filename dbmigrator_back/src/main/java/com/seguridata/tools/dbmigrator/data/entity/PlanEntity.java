package com.seguridata.tools.dbmigrator.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "plans")
@Getter @Setter
public class PlanEntity {
    @Id
    private String id;
    private Integer orderNum;
    private TableEntity sourceTable;
    private TableEntity targetTable;
    private Object startId;
    private Object endId;
    private List<Object> idList;
    private Double progressPercent;
    private String status;
    private Date startDate;
    private Date endDate;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'plan':?#{#self._id} }")
    private List<DefinitionEntity> definitions;
    @DocumentReference(lazy = true)
    private ProjectEntity project;

}
