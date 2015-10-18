package org.respondeco.respondeco.testutil.domain;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.testutil.TestUtil;

/**
 * Created by clemens on 19/08/15.
 */
public class Organizations {

    public static final Organization NEW_MINIMAL = new Organization();
    public static final Organization SAVED_MINIMAL;

    static {
        NEW_MINIMAL.setCreatedDate(null);
        NEW_MINIMAL.setCreatedBy(null);
        NEW_MINIMAL.setLastModifiedDate(null);
        NEW_MINIMAL.setLastModifiedBy(null);
        NEW_MINIMAL.setName("neworganization");
        NEW_MINIMAL.setDescription("a new organization");
        NEW_MINIMAL.setEmail("neworganization@respondeco.org");

        SAVED_MINIMAL = TestUtil.clone(NEW_MINIMAL);
        SAVED_MINIMAL.setId(10000L);
    }

}
