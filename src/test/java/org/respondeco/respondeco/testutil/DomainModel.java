package org.respondeco.respondeco.testutil;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;

import java.util.*;

/**
 * Created by Clemens Puehringer on 19/01/15.
 */
public class DomainModel {

    public User USER_NEW;
    public User USER_WITH_ID;
    public User USER_N1_COMPLETE;

    public Organization ORGANIZATION_NAME_EMPTY;
    public Organization ORGANIZATION_NEW;
    public Organization ORGANIZATION_N1_COMPLETE;

    public DomainModel() {
        USER_NEW = new User() {{
            setLogin("newuser@respondeco.org");
            setEmail("newuser@respondeco.org");
            setPassword("newuser");
        }};
        USER_WITH_ID = new User() {{
            setId(1000L);
            setLogin("userwithid@respondeco.org");
            setEmail("userwithid@respondeco.org");
            setPassword("userwithid");
        }};
        USER_N1_COMPLETE = new User() {{
            setId(1001L);
            setLogin("n1_complete@respondeco.org");
            setEmail("n1_complete@respondeco.org");
            setPassword("n1_complete");
            setTitle("Dr.");
            setFirstName("bob");
            setLastName("ross");
            setGender(Gender.MALE);
            setDescription("a short description");
            setActive(true);
            setActivated(true);
        }};

        ORGANIZATION_NAME_EMPTY = new Organization() {{
            setCreatedDate(null);
            setCreatedBy(null);
            setLastModifiedDate(null);
            setLastModifiedBy(null);
            setName("");
            setDescription("a new organization");
            setEmail("neworganization@respondeco.org");
        }};
        ORGANIZATION_NEW = new Organization() {{
            setCreatedDate(null);
            setCreatedBy(null);
            setLastModifiedDate(null);
            setLastModifiedBy(null);
            setName("neworganization");
            setDescription("a new organization");
            setEmail("neworganization@respondeco.org");
        }};
        ORGANIZATION_N1_COMPLETE = new Organization() {{
            setId(1001L);
            setName("org_n1_complete");
            setDescription("a short description");
            setEmail("org_n1_compelte@org_n1.com");
            setIsNpo(false);
            setVerified(false);
            setActive(true);
            setOwner(USER_N1_COMPLETE);
            USER_N1_COMPLETE.setOrganization(this);
        }};
    }


}
