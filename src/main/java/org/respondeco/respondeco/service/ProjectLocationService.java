package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectLocation;
import org.respondeco.respondeco.repository.ProjectLocationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * This service handles the logic for Project Location
 */
@Service
public class ProjectLocationService {

    private final Logger log = LoggerFactory.getLogger(ProjectLocationService.class);

    private ProjectLocationRepository projectLocationRepository;
    private ProjectRepository projectRepository;

    @Inject
    public ProjectLocationService(ProjectLocationRepository projectLocationRepository,
                                  ProjectRepository projectRepository) {
        this.projectLocationRepository = projectLocationRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Creates a new Location for a given Project with id
     * @param projectId id of the project for which the location is created
     * @param address address in string representation
     * @param latitude coordinates lattitude
     * @param longitude coordinates longitude
     * @return
     */
    public ProjectLocation createProjectLocation(Long projectId, String address, double latitude, double longitude) {

        ProjectLocation projectLocation = new ProjectLocation();

        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        projectLocation.setProject(project);
        projectLocation.setLat(latitude);
        projectLocation.setLng(longitude);
        projectLocation.setAddress(address);

        projectLocation = projectLocationRepository.save(projectLocation);

        return projectLocation;
    }

    public List<ProjectLocation> getAllLocations() {

        return projectLocationRepository.findAll();
    }

}
