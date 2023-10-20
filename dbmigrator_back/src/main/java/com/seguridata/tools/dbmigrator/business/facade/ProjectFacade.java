package com.seguridata.tools.dbmigrator.business.facade;

import com.seguridata.tools.dbmigrator.business.event.InitiateProjectEvent;
import com.seguridata.tools.dbmigrator.business.event.ProjectCreatedEvent;
import com.seguridata.tools.dbmigrator.business.event.StopProjectEvent;
import com.seguridata.tools.dbmigrator.business.exception.BaseCodeException;
import com.seguridata.tools.dbmigrator.business.exception.ObjectLockedException;
import com.seguridata.tools.dbmigrator.business.mapper.ProjectMapper;
import com.seguridata.tools.dbmigrator.business.service.ConnectionService;
import com.seguridata.tools.dbmigrator.business.service.DefinitionService;
import com.seguridata.tools.dbmigrator.business.service.PlanService;
import com.seguridata.tools.dbmigrator.business.service.ProjectService;
import com.seguridata.tools.dbmigrator.data.constant.ProjectStatus;
import com.seguridata.tools.dbmigrator.data.entity.PlanEntity;
import com.seguridata.tools.dbmigrator.data.entity.ProjectEntity;
import com.seguridata.tools.dbmigrator.data.model.ProjectModel;
import com.seguridata.tools.dbmigrator.data.wrapper.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.lang.Boolean.TRUE;


@Component
public class ProjectFacade {

    private final ConnectionService connectionService;
    private final ProjectService projectService;
    private final PlanService planService;
    private final DefinitionService definitionService;
    private final ProjectMapper projectMapper;
    private final ApplicationEventPublisher appEventPublisher;

    @Autowired
    public ProjectFacade(ConnectionService connectionService,
                         ProjectService projectService,
                         PlanService planService,
                         DefinitionService definitionService,
                         ProjectMapper projectMapper,
                         ApplicationEventPublisher appEventPublisher) {
        this.connectionService = connectionService;
        this.projectService = projectService;
        this.planService = planService;
        this.definitionService = definitionService;
        this.projectMapper = projectMapper;
        this.appEventPublisher = appEventPublisher;
    }


    public ResponseWrapper<List<ProjectModel>> getAllProjects() {
        ResponseWrapper<List<ProjectModel>> projectsResponse = new ResponseWrapper<>();
        try {
            List<ProjectEntity> projects = this.projectService.getAllProjects();

            projectsResponse.setCode("00");
            projectsResponse.setData(this.projectMapper.mapProjectModelList(projects));
        } catch (BaseCodeException e) {
            projectsResponse.setCode(e.getCode());
            projectsResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return projectsResponse;
    }

    public ResponseWrapper<ProjectModel> createProject(ProjectModel projectModel) {
        ResponseWrapper<ProjectModel> projectResponse = new ResponseWrapper<>();
        try {
            ProjectEntity project = this.projectMapper.mapProjectEntity(projectModel);
            this.validateConnections(project);
            project = this.projectService.createProject(project);

            ProjectModel newProjectModel = this.projectMapper.mapProjectModel(project);
            newProjectModel.setAutoPopulate(projectModel.getAutoPopulate());
            projectResponse.setCode("00");
            projectResponse.setData(newProjectModel);

            if (TRUE.equals(newProjectModel.getAutoPopulate())) {
                this.appEventPublisher.publishEvent(new ProjectCreatedEvent(this, newProjectModel));
            }
        } catch (BaseCodeException e) {
            projectResponse.setCode(e.getCode());
            projectResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return projectResponse;
    }

    public ResponseWrapper<ProjectModel> getProject(String projectId) {
        ResponseWrapper<ProjectModel> projectResponse = new ResponseWrapper<>();
        try {
            ProjectEntity project = this.projectService.getProject(projectId);

            projectResponse.setCode("00");
            projectResponse.setData(this.projectMapper.mapProjectModel(project));
        } catch (BaseCodeException e) {
            projectResponse.setCode(e.getCode());
            projectResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return projectResponse;
    }

    public ResponseWrapper<ProjectModel> updateProject(String projectId, ProjectModel projectModel) {
        ResponseWrapper<ProjectModel> projectResponse = new ResponseWrapper<>();
        try {
            ProjectEntity existingProject = this.projectService.getProject(projectId);
            this.projectService.validateProjectStatus(existingProject);

            ProjectEntity updatedProject = this.projectMapper.mapProjectEntity(projectModel);
            this.validateConnections(updatedProject);

            if (!Objects.equals(existingProject.getSourceConnection().getId(), updatedProject.getSourceConnection().getId())
                    || !Objects.equals(existingProject.getTargetConnection().getId(), updatedProject.getTargetConnection().getId())) {
                this.planService.deletePlansForProject(existingProject);
            }

            updatedProject = this.projectService.updateProject(existingProject, updatedProject);

            projectResponse.setCode("00");
            projectResponse.setData(this.projectMapper.mapProjectModel(updatedProject));
        } catch (BaseCodeException e) {
            projectResponse.setCode(e.getCode());
            projectResponse.setMessages(Arrays.asList(e.getMessages()));
        }
        return projectResponse;
    }

    public ResponseWrapper<ProjectModel> deleteProject(String projectId) {
        ResponseWrapper<ProjectModel> projectResponse = new ResponseWrapper<>();
        try {
            ProjectEntity existingProject = this.projectService.getProject(projectId);
            this.projectService.validateProjectStatus(existingProject);

            ProjectEntity deletedProject = this.projectService.deleteProject(existingProject);
            List<PlanEntity> deletedPlans = this.planService.deletePlansForProject(deletedProject);

            this.definitionService.deleteDefinitionsByPlanList(deletedPlans);

            projectResponse.setCode("00");
            projectResponse.setData(this.projectMapper.mapProjectModel(deletedProject));
        } catch (BaseCodeException e) {
            projectResponse.setCode(e.getCode());
            projectResponse.setMessages(Arrays.asList(e.getMessages()));
        }

        return projectResponse;
    }

    public ResponseWrapper<Boolean> initiateProjectExecution(String projectId) {
        ResponseWrapper<Boolean> execResponse = new ResponseWrapper<>();
        execResponse.setData(false);
        ProjectEntity project = null;
        try {
            project = this.projectService.getProject(projectId);
            this.projectService.validateProjectStatus(project);

            this.connectionService.validateConnectionStatus(project.getSourceConnection());
            this.connectionService.validateConnectionStatus(project.getTargetConnection());

            this.appEventPublisher.publishEvent(new InitiateProjectEvent(this, project));
            execResponse.setData(true);
            execResponse.setCode("00");
        } catch (BaseCodeException e) {
            execResponse.setCode(e.getCode());
            execResponse.setMessages(Arrays.asList(e.getMessages()));
        } catch (Exception e) {
            execResponse.setCode("07");
            execResponse.setMessages(Collections.singleton(e.getMessage()));
        }

        return execResponse;
    }

    public ResponseWrapper<Boolean> stopProjectExecution(String projectId) {
        ResponseWrapper<Boolean> execResponse = new ResponseWrapper<>();
        execResponse.setData(false);
        ProjectEntity project = null;
        try {
            project = this.projectService.getProject(projectId);
            if (!ProjectStatus.RUNNING.equals(project.getStatus())) {
                throw new ObjectLockedException("Project should be in RUNNING state to stop execution");
            }

            this.appEventPublisher.publishEvent(new StopProjectEvent(this, project));
            execResponse.setData(true);
            execResponse.setCode("00");
        } catch (BaseCodeException e) {
            execResponse.setCode(e.getCode());
            execResponse.setMessages(Arrays.asList(e.getMessages()));
        } catch (Exception e) {
            execResponse.setCode("07");
            execResponse.setMessages(Collections.singleton(e.getMessage()));
        }

        return execResponse;
    }

    private void validateConnections(ProjectEntity project) throws BaseCodeException {
        this.connectionService.getConnection(project.getSourceConnection().getId());
        this.connectionService.getConnection(project.getTargetConnection().getId());
    }
}
