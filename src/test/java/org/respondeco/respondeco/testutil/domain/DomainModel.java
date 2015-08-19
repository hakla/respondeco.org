package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.testutil.TestUtil;

import java.util.Arrays;

/**
 * Created by Clemens Puehringer on 19/01/15.
 */
public class DomainModel {

    public User USER_NEW;
    public User USER_WITH_ID;
    public User USER1;

    public Organization ORGANIZATION_NAME_EMPTY;
    public Organization ORGANIZATION_NEW;
    public Organization ORGANIZATION1;

    public Project PROJECT_NEW;
    public Project PROJECT_NEW_COMPLETE;
    public Project PROJECT1;

    public DomainModel() {
        USER_NEW = TestUtil.clone(Users.NEW_MINIMAL);
        USER_WITH_ID = TestUtil.clone(Users.SAVED_MINIMAL);
        USER1 = TestUtil.clone(Users.SAVED_COMPLETE);

        ORGANIZATION_NAME_EMPTY = TestUtil.clone(Organizations.NEW_MINIMAL);
        ORGANIZATION_NAME_EMPTY.setName("");
        ORGANIZATION_NEW = TestUtil.clone(Organizations.NEW_MINIMAL);
        ORGANIZATION1 = TestUtil.clone(Organizations.SAVED_MINIMAL);

        PROJECT_NEW = TestUtil.clone(Projects.NEW_MINIMAL);
        PROJECT_NEW_COMPLETE = TestUtil.clone(Projects.NEW_COMPLETE);
        PROJECT1 = TestUtil.clone(Projects.SAVED_COMPLETE);

        USER1.setOrganization(ORGANIZATION1);
        ORGANIZATION1.setOwner(USER1);
        ORGANIZATION1.setProjects(Arrays.asList(PROJECT1));
        PROJECT1.setOrganization(ORGANIZATION1);
        PROJECT1.setManager(USER1);
    }


}
