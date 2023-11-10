package com.seguridata.tools.dbmigrator.data.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Document(collection = "plans")
@Getter @Setter
public class PlanEntity implements Serializable {
	private static final long serialVersionUID = -3408169488898832543L;

	@Id
    private String id;
    private Integer orderNum;
    @DocumentReference
    private TableEntity sourceTable;
    @DocumentReference
    private TableEntity targetTable;
    private Long initialSkip;
    private Long rowLimit;
    private Long maxRows;
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
