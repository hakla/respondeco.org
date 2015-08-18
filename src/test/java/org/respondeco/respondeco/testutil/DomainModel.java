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

    public Organization ORGANIZATION_NEW;

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

        ORGANIZATION_NEW = new Organization() {{
            setCreatedDate(null);
            setCreatedBy(null);
            setLastModifiedDate(null);
            setLastModifiedBy(null);
            setName("neworganization");
            setDescription("a new organization");
            setEmail("neworganization@respondeco.org");
        }};
    }


}
