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

@Document(collection = "projects")
@Getter @Setter
public class ProjectEntity implements Serializable {
	private static final long serialVersionUID = -7471974195042863774L;

	@Id
    private String id;
    private String name;
    private String description;
    @DocumentReference
    private ConnectionEntity sourceConnection;
    @DocumentReference
    private ConnectionEntity targetConnection;
    private Date createdAt;
    private Boolean locked;
    private Integer parallelThreads;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'project':?#{#self._id} }")
    private List<PlanEntity> plans;
}
