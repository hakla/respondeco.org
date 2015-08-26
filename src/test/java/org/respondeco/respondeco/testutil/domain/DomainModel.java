package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.testutil.TestUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by Clemens Puehringer on 19/01/15.
 */
public class DomainModel {

    public User USER_NEW;
    public User USER_SAVED_MINIMAL;
    public User USER1_OWNS_ORG1_MANAGES_P1;

    public Organization ORGANIZATION_NEW;
    public Organization ORGANIZATION_SAVED_MINIMAL;
    public Organization ORGANIZATION1_GOVERNS_P1;

    public Project PROJECT_NEW;
    public Project PROJECT_NEW_COMPLETE;
    public Project PROJECT1;

    public List<User> USERS = new ArrayList<>();
    public List<Organization> ORGANIZATIONS = new ArrayList<>();
    public List<Project> PROJECTS = new ArrayList<>();

    public DomainModel() {
        USER_NEW = TestUtil.clone(Users.NEW_MINIMAL);
        USER_SAVED_MINIMAL = TestUtil.clone(Users.SAVED_MINIMAL);
        USER_SAVED_MINIMAL.setAuthorities(TestUtil.clone(Authorities.USER));
        USER1_OWNS_ORG1_MANAGES_P1 = TestUtil.clone(Users.SAVED_COMPLETE);
        USER1_OWNS_ORG1_MANAGES_P1.setAuthorities(TestUtil.clone(Authorities.USER));
        USERS.add(USER_NEW);
        USERS.add(USER_SAVED_MINIMAL);
        USERS.add(USER1_OWNS_ORG1_MANAGES_P1);

        ORGANIZATION_NEW = TestUtil.clone(Organizations.NEW_MINIMAL);
        ORGANIZATION_SAVED_MINIMAL = TestUtil.clone(Organizations.SAVED_MINIMAL);
        ORGANIZATION1_GOVERNS_P1 = TestUtil.clone(Organizations.SAVED_MINIMAL);
        ORGANIZATION1_GOVERNS_P1.setName("organization1");
        ORGANIZATIONS.add(ORGANIZATION_NEW);
        ORGANIZATIONS.add(ORGANIZATION_SAVED_MINIMAL);
        ORGANIZATIONS.add(ORGANIZATION1_GOVERNS_P1);

        PROJECT_NEW = TestUtil.clone(Projects.NEW_MINIMAL);
        PROJECT_NEW_COMPLETE = TestUtil.clone(Projects.NEW_COMPLETE);
        PROJECT1 = TestUtil.clone(Projects.SAVED_COMPLETE);
        PROJECTS.add(PROJECT_NEW);
        PROJECTS.add(PROJECT_NEW_COMPLETE);
        PROJECTS.add(PROJECT1);

        USER_SAVED_MINIMAL.setOrganization(ORGANIZATION_SAVED_MINIMAL);
        ORGANIZATION_SAVED_MINIMAL.setOwner(USER_SAVED_MINIMAL);

        USER1_OWNS_ORG1_MANAGES_P1.setOrganization(ORGANIZATION1_GOVERNS_P1);
        ORGANIZATION1_GOVERNS_P1.setOwner(USER1_OWNS_ORG1_MANAGES_P1);
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(PROJECT1);
        ORGANIZATION1_GOVERNS_P1.setProjects(projects);
        PROJECT1.setOrganization(ORGANIZATION1_GOVERNS_P1);
        PROJECT1.setManager(USER1_OWNS_ORG1_MANAGES_P1);
    }


}
