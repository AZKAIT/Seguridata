package com.seguridata.tools.dbmigrator.web.controller;

import com.seguridata.tools.dbmigrator.business.facade.ProjectFacade;
import com.seguridata.tools.dbmigrator.data.model.ProjectModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("projects")
public class ProjectController {

    private final ProjectFacade projectFacade;

    @Autowired
    public ProjectController(ProjectFacade projectFacade) {
        this.projectFacade = projectFacade;
    }


    @GetMapping
    public ResponseEntity<ResponseWrapper<List<ProjectModel>>> getAllProjects() {
        ResponseWrapper<List<ProjectModel>> projectsResponse = this.projectFacade.getAllProjects();

        if ("00".equals(projectsResponse.getCode())) {
            return ResponseEntity.ok(projectsResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(projectsResponse);
        }
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<ProjectModel>> createProject(@Valid @RequestBody ProjectModel projectModel) {
        ResponseWrapper<ProjectModel> projectResponse = this.projectFacade.createProject(projectModel);

        if ("00".equals(projectResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(projectResponse);
        }
    }

    @GetMapping("{projectId}")
    public ResponseEntity<ResponseWrapper<ProjectModel>> getProject(@PathVariable String projectId) {
        ResponseWrapper<ProjectModel> projectResponse = this.projectFacade.getProject(projectId);
        if ("00".equals(projectResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.OK).body(projectResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(projectResponse);
        }
    }

    @PutMapping("{projectId}")
    public ResponseEntity<ResponseWrapper<ProjectModel>> updateProject(@PathVariable String projectId,
                                                                       @Valid @RequestBody ProjectModel projectModel) {
        ResponseWrapper<ProjectModel> projectResponse = this.projectFacade.updateProject(projectId, projectModel);
        if ("00".equals(projectResponse.getCode())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(projectResponse);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(projectResponse);
        }
    }
}
