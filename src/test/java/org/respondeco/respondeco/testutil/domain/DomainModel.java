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

    public User USER_ADMIN;

    public User USER_NEW;
    public User USER_SAVED_MINIMAL;
    public User USER1_OWNS_ORG1_MANAGES_P1;
    public User USER2_OWNS_ORG2_MANAGES_P2;

    public Organization ORGANIZATION_NEW;
    public Organization ORGANIZATION_SAVED_MINIMAL;
    public Organization ORGANIZATION1_GOVERNS_P1;
    public Organization ORGANIZATION2_GOVERNS_P2;

    public Project PROJECT_NEW;
    public Project PROJECT_NEW_COMPLETE;
    public Project PROJECT1;
    public Project PROJECT2;

    public DomainModel() {
        USER_ADMIN = TestUtil.clone(Users.ADMIN_COMPLETE);

        USER_NEW = TestUtil.clone(Users.NEW_MINIMAL);
        USER_SAVED_MINIMAL = TestUtil.clone(Users.SAVED_MINIMAL);
        USER1_OWNS_ORG1_MANAGES_P1 = TestUtil.clone(Users.SAVED_COMPLETE);
        USER2_OWNS_ORG2_MANAGES_P2 = TestUtil.clone(Users.SAVED_COMPLETE);
        USER2_OWNS_ORG2_MANAGES_P2.setLogin("user2_complete@respondeco.org");
        USER2_OWNS_ORG2_MANAGES_P2.setActivationKey("activation_key_user2");

        ORGANIZATION_NEW = TestUtil.clone(Organizations.NEW_MINIMAL);
        ORGANIZATION_SAVED_MINIMAL = TestUtil.clone(Organizations.SAVED_MINIMAL);
        ORGANIZATION1_GOVERNS_P1 = TestUtil.clone(Organizations.SAVED_MINIMAL);
        ORGANIZATION1_GOVERNS_P1.setName("organization1");
        ORGANIZATION2_GOVERNS_P2 = TestUtil.clone(Organizations.SAVED_MINIMAL);
        ORGANIZATION2_GOVERNS_P2.setName("organization2");

        PROJECT_NEW = TestUtil.clone(Projects.NEW_MINIMAL);
        PROJECT_NEW_COMPLETE = TestUtil.clone(Projects.NEW_COMPLETE);
        PROJECT1 = TestUtil.clone(Projects.SAVED_COMPLETE);
        PROJECT2 = TestUtil.clone(Projects.SAVED_COMPLETE);
        PROJECT2.setName("project2");

        USER_SAVED_MINIMAL.setOrganization(ORGANIZATION_SAVED_MINIMAL);
        ORGANIZATION_SAVED_MINIMAL.setOwner(USER_SAVED_MINIMAL);

        USER1_OWNS_ORG1_MANAGES_P1.setOrganization(ORGANIZATION1_GOVERNS_P1);
        ORGANIZATION1_GOVERNS_P1.setOwner(USER1_OWNS_ORG1_MANAGES_P1);
        ArrayList<Project> projects = new ArrayList<>();
        projects.add(PROJECT1);
        ORGANIZATION1_GOVERNS_P1.setProjects(projects);
        PROJECT1.setOrganization(ORGANIZATION1_GOVERNS_P1);
        PROJECT1.setManager(USER1_OWNS_ORG1_MANAGES_P1);

        USER2_OWNS_ORG2_MANAGES_P2.setOrganization(ORGANIZATION2_GOVERNS_P2);
        ORGANIZATION2_GOVERNS_P2.setOwner(USER2_OWNS_ORG2_MANAGES_P2);
        projects = new ArrayList<>();
        projects.add(PROJECT2);
        ORGANIZATION2_GOVERNS_P2.setProjects(projects);
        PROJECT2.setOrganization(ORGANIZATION2_GOVERNS_P2);
        PROJECT2.setManager(USER2_OWNS_ORG2_MANAGES_P2);
    }


}
