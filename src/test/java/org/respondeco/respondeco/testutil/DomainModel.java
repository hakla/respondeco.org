package org.respondeco.respondeco.testutil;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;

import java.util.*;

/**
 * Created by Clemens Puehringer on 19/01/15.
 */
public class DomainModel {

    private static Long ID = 0L;

    private Map<String, User> usersByLogin;
    private Map<String, Organization> organizationsByName;
    private Map<Long, Organization> organizationsById;
    private Map<Long, Project> projectsById;

    public DomainModel addUser(User user) {
        usersByLogin.put(user.getLogin(), user);
        return this;
    }

    public DomainModel addOrganization(Organization organization) {
        organizationsById.put(organization.getId(), organization);
        organizationsByName.put(organization.getName(), organization);
        return this;
    }

    public DomainModel addProject(Project project) {
        projectsById.put(project.getId(), project);
        return this;
    }

    public User getOneUser() {
        return usersByLogin.values().iterator().next();
    }

    public User getUserWithoutOrg() {
        for(Map.Entry<String, User> entry : usersByLogin.entrySet()) {
            if(entry.getValue().getOrganization() == null) {
                return entry.getValue();
            }
        }
        return null;
    }

    public Collection<User> getUsers() {
        return usersByLogin.values();
    }

    public User getUserByLogin(String login) {
        return usersByLogin.get(login);
    }

    public Organization getOneOrganization() {
        return organizationsById.values().iterator().next();
    }

    public Collection<Organization> getOrganizations() {
        return organizationsById.values();
    }

    public Organization getOrganizationByName(String name) {
        return organizationsByName.get(name);
    }

    public Organization getOrganizationById(Long id) {
        return organizationsById.get(id);
    }

    public Project getOneProject() {
        return projectsById.values().iterator().next();
    }

    public Collection<Project> getProjects() {
        return projectsById.values();
    }

    public Project getProjectById(Long id) {
        return projectsById.get(id);
    }

    private static Long nextId() {
        ID = ID + 1;
        return ID;
    }

    public static User defaultUser() {
        User user = new User();
        user.setId(nextId());
        user.setGender(Gender.UNSPECIFIED);
        user.setActivated(true);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setDescription(String.format("I am test user with id: %d", user.getId()));
        Set<Authority> authorities = new HashSet<>();
        Authority userAuthority = new Authority();
        userAuthority.setName(AuthoritiesConstants.USER);
        authorities.add(userAuthority);
        user.setAuthorities(authorities);
        return user;
    }

    public static Organization defaultOrganization() {
        Organization organization = new Organization();
        organization.setId(nextId());
        organization.setActive(true);
        organization.setVerified(true);
        organization.setDescription("test organization");
        organization.setIsNpo(false);
        organization.setEmail("test@org.at");
        organization.setFollowingUsers(new ArrayList<>());
        organization.setMembers(new ArrayList<>());
        organization.setProjects(new ArrayList<>());
        organization.setResourceMatches(new ArrayList<>());
        organization.setResourceOffers(new ArrayList<>());
        PostingFeed postingFeed = new PostingFeed();
        postingFeed.setId(nextId());
        postingFeed.setPostings(new ArrayList<>());
        organization.setPostingFeed(postingFeed);
        return organization;
    }

    public static Project defaultProject() {
        Project project = new Project();
        project.setId(nextId());
        project.setActive(true);
        project.setConcrete(false);
        project.setPurpose(String.format("test purpose %d", project.getId()));
        project.setStartDate(LocalDate.now().plusDays(1));
        project.setResourceMatches(new ArrayList<>());
        project.setResourceRequirements(new ArrayList<>());
        project.setFollowingUsers(new ArrayList<>());
        PostingFeed postingFeed = new PostingFeed();
        postingFeed.setId(nextId());
        postingFeed.setPostings(new ArrayList<>());
        project.setPostingFeed(postingFeed);
        List<PropertyTag> tags = new ArrayList<>();
        for(int i=0;i<3;i++) {
            PropertyTag tag = new PropertyTag();
            tag.setId(nextId());
            tag.setName(String.format("project%d-tag%d", project.getId(), i));
            tags.add(tag);
        }
        project.setPropertyTags(tags);
        return project;
    }

}
