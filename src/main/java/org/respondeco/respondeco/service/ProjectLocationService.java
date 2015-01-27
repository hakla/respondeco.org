package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectLocation;
import org.respondeco.respondeco.repository.ProjectLocationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
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
    public ProjectLocation createProjectLocation(Long projectId, String address, float latitude, float longitude)
        throws NoSuchProjectException {

        ProjectLocation projectLocation = new ProjectLocation();

        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        projectLocation.setProject(project);
        projectLocation.setLat(latitude);
        projectLocation.setLng(longitude);
        projectLocation.setAddress(address);

        projectLocation = projectLocationRepository.save(projectLocation);

        return projectLocation;
    }

    /**
     * Returns all active ProjectLocations
     * @return list of active ProjectLocations
     */
    public List<ProjectLocation> getAllLocations() {
        return projectLocationRepository.findByActiveIsTrue();
    }


    /**
     * updates the location of a project
     * @param projectId id of the project
     * @param address address of the project
     * @param latitude latitude coordinate of the project
     * @param longitude longitude coordinate of the project
     * @return updated ProjectLocation Object
     */
    public ProjectLocation updateProjectLocation(Long projectId, String address, float latitude, float longitude)
        throws NoSuchProjectException {

        ProjectLocation projectLocation = projectLocationRepository.findByProjectId(projectId);

        if(projectLocation == null) {
            //create new projectLocation
            projectLocation = new ProjectLocation();

            Project project = projectRepository.findByIdAndActiveIsTrue(projectId);

            if(project == null) {
                throw new NoSuchProjectException(projectId);
            }
            projectLocation.setProject(project);
        }

        projectLocation.setAddress(address);
        projectLocation.setLat(latitude);
        projectLocation.setLng(longitude);

        projectLocation = projectLocationRepository.save(projectLocation);

        return projectLocation;
    }


    /**
     * Returns the location of the project
     * @param projectId id of the project
     * @return location of the project
     */
    public ProjectLocation getProjectLocation(Long projectId) {
        ProjectLocation projectLocation = projectLocationRepository.findByProjectId(projectId);

        return projectLocation;
    }


    /**
     * Returns a List of ProjectLocations which are in a specific radius of the position given by
     * latitude and longitude coordinates.
     * @param latitude latitude
     * @param longitude longitude
     * @param radius radius in kilometres, defines the radius of found projects from the position given by latitude and longitude
     * @return a List of ProjectLocations which represents projects near position given by latitude and longitude
     */
    public List<ProjectLocation> getNearProjects(float latitude, float longitude, double radius)
        throws IllegalValueException {

        if(radius <= 0) {
            throw new IllegalValueException("nearProjects.error.radius", "Der Radius muss größer als 0 sein");
        }

        List<ProjectLocation> projectLocationList = new ArrayList<>();

        List<Object[]> objectArrayList = projectLocationRepository.findNearProjects(latitude, longitude, radius);
        for(Object[] objArray : objectArrayList) {
            //Create the object manually, because native sql query returns a list of object arrays
            ProjectLocation projectLocation = new ProjectLocation();
            projectLocation.setId(((BigInteger)objArray[0]).longValue());
            projectLocation.setLat((float)objArray[1]);
            projectLocation.setLng((float)objArray[2]);
            projectLocation.setAddress((String)objArray[3]);

            long projectId = ((BigInteger) objArray[4]).longValue();
            Project project = projectRepository.findOne(projectId);
            if(project == null) {
                throw new NoSuchProjectException(projectId);
            }

            projectLocation.setProject(project);

            projectLocationList.add(projectLocation);
        }

        return projectLocationList;
    }

}
