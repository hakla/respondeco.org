package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.testutil.TestUtil;

import static org.respondeco.respondeco.testutil.TestUtil.blank;

/**
 * Created by clemens on 19/08/15.
 */
public class Organizations {

    public static final Organization NEW_MINIMAL;
    public static final Organization SAVED_MINIMAL;
    public static final Organization SAVED_DEACTIVATED;

    static {
        NEW_MINIMAL = blank(Organization.class);
        NEW_MINIMAL.setName("new_organization");
        NEW_MINIMAL.setDescription("a new organization");
        NEW_MINIMAL.setEmail("neworganization@respondeco.org");

        SAVED_MINIMAL = new Organization();
        SAVED_MINIMAL.setId(10000L);
        SAVED_MINIMAL.setName("saved_organization");
        SAVED_MINIMAL.setDescription("a saved organization");
        SAVED_MINIMAL.setEmail("savedorganization@respondeco.org");

        SAVED_DEACTIVATED = new Organization();
        SAVED_DEACTIVATED.setId(10001L);
        SAVED_DEACTIVATED.setName("deactivated_organization");
        SAVED_DEACTIVATED.setDescription("a deactivated organization");
        SAVED_DEACTIVATED.setEmail("deactivatedorganization@respondeco.org");
        SAVED_DEACTIVATED.setActive(false);
    }

}
