package org.respondeco.respondeco.service;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.path.NumberPath;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectLocation;
import org.respondeco.respondeco.domain.QProjectLocation;
import org.respondeco.respondeco.repository.ProjectLocationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import scala.math.BigInt;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.mysema.query.types.expr.MathExpressions.*;

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

    /**
     * Returns all active ProjectLocations
     * @return
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
    public ProjectLocation updateProjectLocation(Long projectId, String address, double latitude, double longitude)
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


    public List<ProjectLocation> getNearProjects(double latitude, double longitude, double radius) {

        /* //query dsl versuch
        QProjectLocation qProjectLocation = QProjectLocation.projectLocation;

        NumberPath<Double> lat = qProjectLocation.lat;
        NumberPath<Double> lng = qProjectLocation.lng;
        NumberPath<Double> distance = null;
        NumberExpression<Double> formula =
            (acos(cos(radians(Expressions.constant(48.2083)))
                .multiply(cos(radians(lat))
                    .multiply(cos(radians(lng).subtract(radians(Expressions.constant(16.3677)))
                        .add(sin(radians(Expressions.constant(48.2083)))
                            .multiply(sin(radians(lat))))))))
                .multiply(Expressions.constant(6371)));

        Predicate where = qProjectLocation.in(new JPASubQuery().from(qProjectLocation).where(formula.lt(5000)).list(qProjectLocation));
        List<ProjectLocation> projectLocations = projectLocationRepository.findAll(where, new PageRequest(0,20)).getContent();

*/


        List<ProjectLocation> projectLocationList = new ArrayList<>();

        List<Object[]> objectArrayList = projectLocationRepository.findNearProjects();
        for(Object[] objArray : objectArrayList) {
            ProjectLocation projectLocation = new ProjectLocation();
            projectLocation.setId(((BigInteger)objArray[0]).longValue());
            projectLocation.setLat((double)objArray[1]);
            projectLocation.setLng((double) objArray[2]);
            projectLocation.setAddress((String)objArray[3]);

            Project project = projectRepository.findOne(((BigInteger) objArray[4]).longValue());
            projectLocation.setProject(project);

            projectLocationList.add(projectLocation);
        }

        return projectLocationList;
    }

}
