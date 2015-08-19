package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
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

    //private ProjectLocationRepository projectLocationRepository;
    private ProjectRepository projectRepository;

//    @Inject
//    public ProjectLocationService(ProjectLocationRepository projectLocationRepository,
//                                  ProjectRepository projectRepository) {
//        this.projectLocationRepository = projectLocationRepository;
//        this.projectRepository = projectRepository;
//    }

    /**
     * Returns a List of ProjectLocations which are in a specific radius of the position given by
     * latitude and longitude coordinates.
     * @param latitude latitude
     * @param longitude longitude
     * @param radius radius in kilometres, defines the radius of found projects from the position given by latitude and longitude
     * @return a List of ProjectLocations which represents projects near position given by latitude and longitude
     */
//    public List<ProjectLocation> getNearProjects(float latitude, float longitude, double radius)
//        throws IllegalValueException {
//
//        if(radius <= 0) {
//            throw new IllegalValueException("nearProjects.error.radius", "Der Radius muss größer als 0 sein");
//        }
//
//        List<ProjectLocation> projectLocationList = new ArrayList<>();
//
//        List<Object[]> objectArrayList = projectLocationRepository.findNearProjects(latitude, longitude, radius);
//        for(Object[] objArray : objectArrayList) {
//            //Create the object manually, because native sql query returns a list of object arrays
//            ProjectLocation projectLocation = new ProjectLocation();
//            projectLocation.setId(((BigInteger)objArray[0]).longValue());
//            projectLocation.setLat((float)objArray[1]);
//            projectLocation.setLng((float)objArray[2]);
//            projectLocation.setAddress((String)objArray[3]);
//
//            long projectId = ((BigInteger) objArray[4]).longValue();
//            Project project = projectRepository.findOne(projectId);
//            if(project == null) {
//                throw new NoSuchEntityException(projectId);
//            }
//
//            projectLocation.setProject(project);
//
//            projectLocationList.add(projectLocation);
//        }
//
//        return projectLocationList;
//    }

}
