package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.PropertyTagRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemens on 15/11/14.
 */

@Service
@Transactional
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private ProjectRepository projectRepository;
    private UserService userService;
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;
    private PropertyTagRepository propertyTagRepository;

    @Inject
    public ProjectService(ProjectRepository projectRepository,
                          UserService userService, UserRepository userRepository,
                          OrganizationRepository organizationRepository,
                          PropertyTagRepository propertyTagRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.propertyTagRepository = propertyTagRepository;
    }

    public Project create(String name, String purpose, boolean isConcrete, LocalDate startDate,
                          LocalDate endDate, byte[] logo, List<String> propertyTags,
                          List<ResourceRequirementDTO> resourceRequirements) throws OperationForbiddenException {
        sanityCheck(isConcrete, startDate, endDate);
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrgId() == null) {
            throw new OperationForbiddenException("Current user does not belong to an Organization");
        }
        Organization organization = organizationRepository.findOne(currentUser.getOrgId());
        if(organization == null) {
            throw new IllegalArgumentException("Organization does not exist: " + currentUser.getOrgId());
        }

        Project newProject = new Project();
        newProject.setManager(currentUser);
        newProject.setOrganization(organization);
        newProject.setName(name);
        newProject.setPurpose(purpose);
        newProject.setConcrete(isConcrete);
        newProject.setStartDate(startDate);
        newProject.setEndDate(endDate);
        if(logo != null) {
            ProjectLogo projectLogo = new ProjectLogo();
            projectLogo.setData(logo);
            newProject.setProjectLogo(projectLogo);
        }
        List<PropertyTag> tags = getPropertyTags(propertyTags);
        newProject.setPropertyTags(tags);

        projectRepository.save(newProject);
        return newProject;
    }

    public Project update(Long id, String name, String purpose, boolean isConcrete, LocalDate startDate,
                        LocalDate endDate, byte[] logo) throws OperationForbiddenException {
        sanityCheck(isConcrete, startDate, endDate);
        if(id == null) {
            throw new IllegalArgumentException("Project id must not be null");
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrgId() == null) {
            throw new OperationForbiddenException("Current user does not belong to an Organization");
        }
        Organization organization = organizationRepository.findOne(currentUser.getOrgId());
        if(organization == null) {
            throw new IllegalArgumentException("Organization does not exist: " + currentUser.getOrgId());
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new IllegalArgumentException("Project does not exist: " + id);
        }
        if(project.getOrganization().getId().equals(organization.getId()) == false) {
            throw new OperationForbiddenException("Project " + project +
                    " is not a project from organization " + organization);
        }
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.getId().equals(organization.getOwner()) == false) {
                throw new OperationForbiddenException("Current user does have permission to alter project " + project);
            }
        }

        project.setName(name);
        project.setPurpose(purpose);
        project.setConcrete(isConcrete);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        if(logo != null) {
            ProjectLogo projectLogo = new ProjectLogo();
            projectLogo.setData(logo);
            project.setProjectLogo(projectLogo);
        }
        projectRepository.save(project);
        return project;
    }

    public ProjectResponseDTO findById(Long id, String fields) {
        List<String> fieldNames = new ArrayList<>();
        if(fields != null) {
            for(String s : fields.split(",")) {
                fieldNames.add(s.trim());
            }
        }
        if(fieldNames.size() == 0) {
            fieldNames.addAll(ProjectResponseDTO.DEFAULT_FIELDS);
        }
        Project p = projectRepository.findByIdAndActiveIsTrue(id);
        ProjectResponseDTO responseDTO = null;
        if(p != null) {
            responseDTO = mapFields(p, fieldNames);
        }
        return responseDTO;
    }

    public Project setManager(Long id, String newManagerLogin) throws NoSuchUserException, OperationForbiddenException {
        User newManager = userRepository.findByLogin(newManagerLogin);
        if(newManager == null) {
            throw new NoSuchUserException("no such user: " + id);
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new IllegalArgumentException("no such project: " + id);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.getId().equals(project.getOrganization().getOwner()) == false) {
                throw new OperationForbiddenException("current user has no authority to " +
                        "change the project manager of project " + id);
            }
        }
        if(project.getOrganization().getId().equals(newManager.getOrgId()) == false) {
            throw new IllegalArgumentException("new manager does not belong to organization: " +
                    project.getOrganization());
        }
        project.setManager(newManager);
        return projectRepository.save(project);
    }

    public Project delete(Long id) throws OperationForbiddenException {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new IllegalArgumentException("no such project: " + id);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.getId().equals(project.getOrganization().getOwner()) == false) {
                throw new OperationForbiddenException("current user has no authority to " +
                        "delete project " + id);
            }
        }
        project.setActive(false);
        projectRepository.save(project);
        return project;
    }

    public List<ProjectResponseDTO> findProjects(String name, String tagsString, Integer offset, Integer limit, String fields) {
        if(name == null) {
            name = "";
        }
        if(offset == null) {
            offset = 0;
        }
        if(limit == null) {
            limit = 20;
        }
        List<String> tags = new ArrayList<>();
        if(tagsString != null) {
            for(String s : tagsString.split(",")) {
                tags.add(s.trim());
            }
        }
        List<String> fieldNames = new ArrayList<>();
        if(fields != null) {
            for(String s : fields.split(",")) {
                fieldNames.add(s.trim());
            }
        }
        if(fieldNames.size() == 0) {
            fieldNames.addAll(ProjectResponseDTO.DEFAULT_FIELDS);
        }

        return mapResponses(projectRepository.findByNameAndTags(name, tags, null), fieldNames);
    }

    private void sanityCheck(boolean isConcrete, LocalDate startDate, LocalDate endDate) {
        if(isConcrete == true) {
            if(startDate == null) {
                throw new IllegalArgumentException("start date cannot be null if project is concrete");
            }
            if(endDate == null) {
                throw new IllegalArgumentException("end date cannot be null if project is concrete");
            }
            if(endDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("end date cannot be before before now");
            }
            if(endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("end date cannot be before start date");
            }
        }
     }

    private List<PropertyTag> getPropertyTags(List<String> tags) {
        List<PropertyTag> propertyTags = new ArrayList<>();
        if(tags == null) {
            return propertyTags;
        }
        PropertyTag tag;
        for(String s : tags) {
            tag = propertyTagRepository.findByName(s);
            if(tag == null) {
                tag = new PropertyTag();
                tag.setName(s);
                propertyTagRepository.save(tag);
            }
            propertyTags.add(tag);
        }
        return propertyTags;
    }

    private List<ProjectResponseDTO> mapResponses(List<Project> projects, List<String> fieldNames) {
        List<ProjectResponseDTO> responseDTOs = new ArrayList<>();
        for(Project p : projects) {
            responseDTOs.add(mapFields(p, fieldNames));
        }
        return responseDTOs;
    }

    private ProjectResponseDTO mapFields(Project project, List<String> fieldNames) {
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
        if(fieldNames.contains("id")) {
            responseDTO.setId(project.getId());
        }
        if(fieldNames.contains("name")) {
            responseDTO.setName(project.getName());
        }
        if(fieldNames.contains("purpose")) {
            responseDTO.setPurpose(project.getPurpose());
        }
        if(fieldNames.contains("concrete")) {
            responseDTO.setConcrete(project.isConcrete());
        }
        if(fieldNames.contains("start_date")) {
            responseDTO.setStartDate(project.getStartDate());
        }
        if(fieldNames.contains("end_date")) {
            responseDTO.setEndDate(project.getEndDate());
        }
        if(fieldNames.contains("organization")) {
            responseDTO.setOrganization(project.getOrganization());
        }
        if(fieldNames.contains("organization_id")) {
            responseDTO.setOrganizationId(project.getOrganization().getId());
        }
        if(fieldNames.contains("manager")) {
            responseDTO.setManager(project.getManager());
        }
        if(fieldNames.contains("manager_id")) {
            responseDTO.setManagerId(project.getManager().getId());
        }
        if(fieldNames.contains("property_tags")) {
            responseDTO.setPropertyTags(project.getPropertyTags());
        }
        if(fieldNames.contains("resource_requirements")) {
            responseDTO.setResourceRequirements(project.getResourceRequirements());
        }
        return responseDTO;
    }

}
