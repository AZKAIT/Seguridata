package com.seguridata.tools.dbmigrator.data.entity;

import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.Date;
import java.util.List;

@Document(collection = "projects")
@Getter @Setter
public class ProjectEntity {
    @Id
    private String id;
    private String name;
    private String description;
    @DocumentReference
    private ConnectionEntity sourceConnection;
    @DocumentReference
    private ConnectionEntity targetConnection;
    private Date createdAt;
    private ProjectStatus status;
    private Date lastStatusDate;
    @ReadOnlyProperty
    @DocumentReference(lookup="{'project':?#{#self._id} }")
    private List<PlanEntity> plans;
}
