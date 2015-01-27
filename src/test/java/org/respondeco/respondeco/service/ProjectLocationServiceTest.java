package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectLocation;
import org.respondeco.respondeco.repository.ProjectLocationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Benjamin Fraller on 04.01.2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ProjectLocationServiceTest {

    @Mock
    private ProjectRepository projectRespositoryMock;

    @Mock
    private ProjectLocationRepository projectLocationRepositoryMock;

    private ProjectLocationService projectLocationService;

    private Project project, project2;
    private ProjectLocation projectLocation,projectLocation2;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        projectLocationService = new ProjectLocationService(projectLocationRepositoryMock, projectRespositoryMock);

        project = new Project();
        project.setId(1L);

        project2 = new Project();
        project2.setId(2L);

        projectLocation = new ProjectLocation();
        projectLocation.setProject(project);
        projectLocation.setAddress("address");
        projectLocation.setLat(10.0f);
        projectLocation.setLng(15.0f);
        projectLocation.setId(1L);

        projectLocation2 = new ProjectLocation();
        projectLocation2.setProject(project2);
        projectLocation2.setAddress("address2");
        projectLocation2.setLat(10.0f);
        projectLocation2.setLng(10.0f);
        projectLocation2.setId(2L);
    }

    @Test
    public void testCreateProjectLocation_shouldCreateProjectLocation() throws Exception {

        doReturn(project).when(projectRespositoryMock).findByIdAndActiveIsTrue(1L);
        doReturn(projectLocation).when(projectLocationRepositoryMock).save(any(ProjectLocation.class));

        ProjectLocation location = projectLocationService.createProjectLocation(1L, "address", 10.0f, 15.0f);

        assertEquals(location.getId().longValue(), 1L);
        assertEquals(location.getAddress(), "address");
        assertEquals(location.getLat(), 10.0, 0.1);
        assertEquals(location.getLng(), 15.0, 0.1);
        assertEquals(location.getProject().getId().longValue(), 1L);
    }

    @Test(expected = NoSuchProjectException.class)
    public void testCreateProjectLocation_shouldThrowException() throws Exception {
        doReturn(null).when(projectRespositoryMock).findByIdAndActiveIsTrue(1L);
        projectLocationService.createProjectLocation(1L, "address", 10.0f, 15.0f);
    }

    @Test
    public void testGetAllLocations_shouldReturnProjectLocations() throws Exception {
        doReturn(Arrays.asList(projectLocation, projectLocation2)).when(projectLocationRepositoryMock).findByActiveIsTrue();
        List<ProjectLocation> projectLocations = projectLocationService.getAllLocations();

        verify(projectLocationRepositoryMock, times(1)).findByActiveIsTrue();
        assertEquals(projectLocations, Arrays.asList(projectLocation, projectLocation2));
    }

    @Test
    public void testUpdateProjectLocation_shouldUpdateProjectLocation() throws Exception {
        doReturn(projectLocation).when(projectLocationRepositoryMock).findByProjectId(1L);
        doAnswer(invocation -> {
                ProjectLocation location = new ProjectLocation();
                location.setId(1L);
                location.setAddress("newaddress");
                location.setLat(20.0f);
                location.setLng(25.0f);
                location.setProject(project);

                return location;
            }).when(projectLocationRepositoryMock).save(any(ProjectLocation.class));

        ProjectLocation updatedLocation = projectLocationService.updateProjectLocation(1L, "newaddress", 20.0f, 25.0f);

        assertEquals(updatedLocation.getAddress(), "newaddress");
        assertEquals(updatedLocation.getLat(), 20.0, 0.1);
        assertEquals(updatedLocation.getLng(), 25.0, 0.1);

        verify(projectLocationRepositoryMock, times(1)).findByProjectId(1L);
        verify(projectLocationRepositoryMock, times(1)).save(any(ProjectLocation.class));
    }

    @Test(expected = NoSuchProjectException.class)
    public void testUpdateProjectLocation_shouldThrowNoSuchProjectException() throws Exception {
        doReturn(null).when(projectLocationRepositoryMock).findByProjectId(1L);
        doReturn(null).when(projectLocationRepositoryMock).save(any(ProjectLocation.class));

        projectLocationService.updateProjectLocation(1L, "newaddress", 20.0f, 25.0f);
    }

    @Test
    public void testGetNearProjects_shouldReturnNearProjects() throws Exception {
        List<Object[]> objectArrayList = new ArrayList<>();
        Object[] object = {new BigInteger("1"), new Float(20.0), new Float(15.0), new String("address"), new BigInteger("1")};
        objectArrayList.add(object);

        doReturn(objectArrayList).when(projectLocationRepositoryMock).findNearProjects(anyFloat(), anyFloat(), anyDouble());
        doReturn(project).when(projectRespositoryMock).findOne(anyLong());

        List<ProjectLocation> projectLocations = projectLocationService.getNearProjects(20.0f,10.0f,100.0);

        assertEquals(projectLocations.get(0).getAddress(), "address");
        assertEquals(projectLocations.get(0).getLat(), 20.0, 0.1);
        assertEquals(projectLocations.get(0).getLng(), 15.0, 0.1);
        assertEquals(projectLocations.get(0).getId().longValue(), 1L);
    }

    @Test(expected = NoSuchProjectException.class)
    public void testGetNearProjects_shouldThrowNoSuchProjectException() throws Exception {
        List<Object[]> objectArrayList = new ArrayList<>();
        Object[] object = {new BigInteger("1"), new Float(20.0), new Float(15.0), new String("address"), new BigInteger("1")};
        objectArrayList.add(object);

        doReturn(objectArrayList).when(projectLocationRepositoryMock).findNearProjects(anyFloat(), anyFloat(), anyDouble());
        doReturn(null).when(projectRespositoryMock).findOne(anyLong());

        projectLocationService.getNearProjects(20.0f,10.0f,100);
    }

    @Test(expected = IllegalValueException.class)
    public void testGetNearProjects_shouldThrowIllegalValueException() throws Exception {
        projectLocationService.getNearProjects(20.0f,10.0f,-1);
    }

    @Test
    public void getProjectLocation_shouldReturnProjectLocation() throws Exception {
        doReturn(projectLocation).when(projectLocationRepositoryMock).findByProjectId(1L);

        ProjectLocation location = projectLocationService.getProjectLocation(1L);

        assertEquals(location, projectLocation);
        verify(projectLocationRepositoryMock, times(1)).findByProjectId(anyLong());
    }

}
